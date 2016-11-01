//$Id$
package com.zoho.oauth;

import java.util.Calendar;
import java.util.Properties;

import org.json.JSONObject;


public class OAuthUtil {

	private static String IAMURL;
	private static Properties prop ; 

	static String getProperty(String property)
	{
		if(property!=null && getProp()!=null)
		{
			return (String) getProp().get(property);
		}
		return null;
	}
	
	/*
	 * Url Construction
	 */
	static String getGrantURL()
	{
		return getIAMURL()+"/oauth/v2/auth";
	}
	static String getTokenURL()
	{
		return getIAMURL()+"/oauth/v2/token";
	}
	static String getRefreshTokenURL()
	{
		return getIAMURL()+"/oauth/v2/token";
	}
	static String getRevokeTokenURL()
	{
		return getIAMURL()+"/oauth/v2/token/revoke";
	}
	static String getIAMURL() 
	{
		if(IAMURL==null)
		{
			IAMURL =  getProperty(OAuthConstants.IAMURL);
		}
		return IAMURL;
	}
	/*
	 * getOAuthProp
	 */
	static String getCRMScope()
	{
		return OAuthUtil.getProperty(OAuthConstants.SCOPE);
	}
	static String getClientID()
	{
		return OAuthUtil.getProperty(OAuthConstants.CLIENT_ID);
	}
	static String getClientSecret()
	{
		return OAuthUtil.getProperty(OAuthConstants.CLIENT_SECRET);
	}
	static String getRedirectURL()
	{
		return OAuthUtil.getProperty(OAuthConstants.REDIRECT_URI);
	}
	static String getAccessType()
	{
		return OAuthUtil.getProperty(OAuthConstants.ACCESS_TYPE);
	}
	/*
	 * getter setter
	 */
	static Properties getProp() {
		return prop;
	}
	static void setProp(Properties prop) {
		OAuthUtil.prop = prop;
	}
	public static OAuthParams generateToken(String code)throws Exception
	{

		/*
		 * Generate Access and refresh Token 
		 */
		HTTPConnector conn = new HTTPConnector();
		conn.setUrl(OAuthUtil.getTokenURL());
		
		conn.addParam(OAuthConstants.CODE, code);
		conn.addParam(OAuthConstants.CLIENT_ID, OAuthUtil.getClientID());
		conn.addParam(OAuthConstants.CLIENT_SECRET, OAuthUtil.getClientSecret());
		conn.addParam(OAuthConstants.REDIRECT_URI, OAuthUtil.getRedirectURL());
		conn.addParam(OAuthConstants.GRANT_TYPE, OAuthConstants.GRANT_TYPE_AUTH_CODE);
		
		String resp = conn.post();
		JSONObject response = new JSONObject(resp);
		if(response.has(OAuthConstants.ACCESS_TOKEN))
		{
			Calendar expiryTime = Calendar.getInstance();
			
			Integer expiresIn = (Integer) response.get(OAuthConstants.EXPIRES_IN);
			expiryTime.add(Calendar.MILLISECOND, expiresIn);
			
			
			String accessToken = (String)response.get(OAuthConstants.ACCESS_TOKEN);
			
			
			OAuthParams params = new OAuthParams();
			params.setAccessToken(accessToken);
			/*
			 * Fetch refresh token only if access type is set to Offline
			 */
			if(OAuthConstants.ACCESS_TYPE_OFFLINE.equals(OAuthUtil.getAccessType()))
			{
				String refreshToken = (String) response.get(OAuthConstants.REFRESH_TOKEN);
				params.setRefreshToken(refreshToken);
			}
			
			params.setExpiryTime(expiryTime.getTimeInMillis());
			/*
			 * Persist Token Information in DB 
			 */
			PersistenceHandler handler = getPersistenceImplClass();
			if(handler!=null)
			{
				handler.saveOAuthData(params);
			}
			return params;
		}
		else
		{
			throw new Exception("Access Token not available");
		}
	}
	public static OAuthParams refreshAccessToken(String refreshToken)throws Exception
	{
		HTTPConnector conn = new HTTPConnector();
		conn.setUrl(OAuthUtil.getRefreshTokenURL());
		
		conn.addParam(OAuthConstants.REFRESH_TOKEN, refreshToken);
		conn.addParam(OAuthConstants.CLIENT_ID, OAuthUtil.getClientID());
		conn.addParam(OAuthConstants.CLIENT_SECRET, OAuthUtil.getClientSecret());
		conn.addParam(OAuthConstants.REDIRECT_URI, OAuthUtil.getRedirectURL());
		conn.addParam(OAuthConstants.GRANT_TYPE, OAuthConstants.GRANT_TYPE_REFRESH);
		
		String resp = conn.post();
		JSONObject response = new JSONObject(resp);
		if(response.has(OAuthConstants.ACCESS_TOKEN))
		{
			Calendar expiryTime = Calendar.getInstance();
			
			Integer expiresIn = (Integer) response.get(OAuthConstants.EXPIRES_IN);
			expiryTime.add(Calendar.MILLISECOND, expiresIn);
			
			
			String accessToken = (String)response.get(OAuthConstants.ACCESS_TOKEN);
			
			OAuthParams params = new OAuthParams();
			params.setAccessToken(accessToken);
			params.setRefreshToken(refreshToken);
			params.setExpiryTime(expiryTime.getTimeInMillis());
			
			/*
			 * Persist Token Information in DB 
			 */
			PersistenceHandler handler = getPersistenceImplClass();
			if(handler!=null)
			{
				handler.updateOAuthData(params);
			}
			
			return params;
		}
		else
		{
			throw new Exception("Access Token not available");
		}
	}
	public static Boolean revokeRefreshToken(String refreshToken)throws Exception
	{
		HTTPConnector conn = new HTTPConnector();
		conn.setUrl(OAuthUtil.getRevokeTokenURL());
		conn.addParam(OAuthConstants.TOKEN, refreshToken);
		String resp = conn.post();
		JSONObject obj = new JSONObject(resp);
		if(obj.has("status") && "success".equals(obj.get("status")))
		{
			
			/*
			 * Persist Token Information in DB 
			 */
			PersistenceHandler handler = getPersistenceImplClass();
			if(handler!=null)
			{
				handler.deleteOAuthData(refreshToken);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	public static Boolean isTokenValid(OAuthParams params)
	{
		if(params!=null)
		{
			if(params.getAccessToken()!=null)
			{
				if( params.getExpiryTime() - System.currentTimeMillis() < 60000L)
				{
					return Boolean.TRUE;
				}

			}
		}
		return Boolean.FALSE;
	}
	private static PersistenceHandler getPersistenceImplClass()
	{
		try 
		{
			String persistenceImpl = OAuthUtil.getProperty(OAuthConstants.PERSISTENCE_IMPL);
			PersistenceHandler handler = (PersistenceHandler) Class.forName(persistenceImpl).newInstance();
			return handler;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
