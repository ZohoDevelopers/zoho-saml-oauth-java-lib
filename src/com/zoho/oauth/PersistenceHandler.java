//$Id$
package com.zoho.oauth;
public interface PersistenceHandler {
	public void saveOAuthData(OAuthParams params)throws Exception;
	public void updateOAuthData(OAuthParams params)throws Exception;
	public OAuthParams getOAuthData(Object obj)throws Exception;
	public void deleteOAuthData(String refreshToken)throws Exception;
}
