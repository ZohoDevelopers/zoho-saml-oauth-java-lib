//$Id$
package com.zoho.oauth;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;

public class OAuth extends HttpServlet {
	static String dispatchTo;
	public static final String OAUTH_PARAMS = "OAUTH_PARAMS";
	public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException  
	{
		try
		{
			String state = req.getParameter(OAuthConstants.STATE);
			/*
			 * When state variable is not present initiate OAuth grantToken request
			 */
			if(state==null)
			{
				URIBuilder url = new URIBuilder(OAuthUtil.getGrantURL());
				
				url.setParameter(OAuthConstants.SCOPES, OAuthUtil.getCRMScope())
					.setParameter(OAuthConstants.CLIENT_ID, OAuthUtil.getClientID())
					.setParameter(OAuthConstants.REDIRECT_URI, OAuthUtil.getRedirectURL())
					.setParameter(OAuthConstants.STATE, OAuthConstants.STATE_OBTAINING_GRANT_TOKEN)
					.setParameter(OAuthConstants.RESPONSE_TYPE, OAuthConstants.RESPONSE_TYPE_CODE)
					.setParameter(OAuthConstants.ACCESS_TYPE, OAuthUtil.getAccessType())
					.setParameter(OAuthConstants.PROMPT, OAuthConstants.PROMPT_CONSENT);
				
				res.sendRedirect(url.toString());
				return;
			}
			/*
			 * When state is present check for Authorization Code and request Access and Refresh Token
			 */
			else if(OAuthConstants.STATE_OBTAINING_GRANT_TOKEN.equals(state))
			{
				String code = req.getParameter(OAuthConstants.CODE);
				if(code!=null)
				{
					OAuthParams params = OAuthUtil.generateToken(code);
					if(dispatchTo!=null)
					{
						req.setAttribute(OAuth.OAUTH_PARAMS, params);
				        RequestDispatcher rd =req.getRequestDispatcher(OAuth.dispatchTo);  
				        rd.forward(req, res);
				        return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void init(ServletConfig config) throws ServletException {
		/*
		 * Read and set properties
		 */
		String OAuthConfFolder= config.getServletContext().getRealPath("/WEB-INF/OAuthConfig");
		FileInputStream oauthConfigFile;
		try 
		{
			oauthConfigFile = new FileInputStream(OAuthConfFolder+"/OAUTHConfig.properties");
			OAuthUtil.setProp(new Properties());
			OAuthUtil.getProp().load(oauthConfigFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		OAuth.dispatchTo = config.getInitParameter(OAuthConstants.DISPATCH_TO);
		
		super.init(config);
		
	}
}
