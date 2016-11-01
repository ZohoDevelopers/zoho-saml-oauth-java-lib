package com.zoho.saml;


public class SamlUtil {
	public static User getCurrentUser()
	{
		User user = UserThreadLocal.get();
		return user;
	}
}
