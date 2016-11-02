//$Id$
package com.test;

import com.zoho.oauth.OAuthParams;
import com.zoho.oauth.PersistenceHandler;

public class PersistenceHandlerImpl implements PersistenceHandler {

	public void saveOAuthData(OAuthParams params) throws Exception {
		System.out.println("saveOAuthData:param:"+params);
	}

	public void updateOAuthData(OAuthParams params) throws Exception {
		System.out.println("updateOAuthData:Param:"+params);

	}

	public OAuthParams getOAuthData(Object obj) throws Exception {
		System.out.println("getOAuthData:Param:"+obj);
		return null;
	}

	public void deleteOAuthData(String refreshToken) throws Exception {
		System.out.println("deleteOAuthData:Param:"+refreshToken);

		
	}
}
