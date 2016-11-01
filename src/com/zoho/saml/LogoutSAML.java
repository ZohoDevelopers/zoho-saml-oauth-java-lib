package com.zoho.saml;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutSAML{
	
	public void service(HttpServletRequest req, HttpServletResponse res)throws IOException 
	{
		req.getSession().invalidate();
		Util.redirectToLoginPage(res);
		return;
	}
}
