package com.zoho.saml;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Samlfilter implements Filter {
	private static String  excludePattern =null;
	@Override
	public void destroy() {
	}

	@Override
	/*
	 * all Request with suffix .do goes through SamlFilter and user Session is validated
	 */
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		try
		{
			HttpServletRequest hreq = (HttpServletRequest)request;
			HttpServletResponse resp = (HttpServletResponse)response;
			
			/*
			 * Skip samlFilter for requestURI matching Exclude pattern 
			 */
			String reqURI = hreq.getRequestURI();
			if(Pattern.matches(excludePattern,reqURI))
			{
				 chain.doFilter(request, response);
				 return;
			}
			
			User user = (User)hreq.getSession().getAttribute(SAMLConstants.USER_IN_SESSION);
			/*
			 * Set CurrentUser Object in threadLocal
			 */
			if(user!=null)
			{
				UserThreadLocal.set(user);
			}
			/*
			 * When user is not in session, Redirect to LoginPage
			 */
			else
			 {
				 Util.redirectToLoginPage(resp);
				 return;
			 }
			 chain.doFilter(request, response);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			/*
			 * Cleanup UserThreadLocal before serving Response to client 
			 */
			 UserThreadLocal.cleanUp();
		}
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException 
	{
		InputStream samlConfigFile = null;
		try
		{
			/*
			 * Absolute path for SAMLConf directory 
			 */
			String samlConfDir= arg0.getServletContext().getRealPath("/WEB-INF")+"/SAMLConf/";
			Util.setSamlConfDir(samlConfDir);
			/*
			 * Initialize samlConfig Properties
			 */
			samlConfigFile = new FileInputStream(samlConfDir+"samlConfig.properties");
			Util.setSamlProp(new Properties());
			Util.getSamlProp().load(samlConfigFile);

			/*
			 * Initialize SamlRequest Template
			 */
    		String samlRequestTemplate = samlConfDir+Util.getSAMLProperty(SAMLConstants.SAML_REQUEST_TEMPLATE_PATH);
        	String samlresponse = Util.getFileAsString(new java.io.File(samlRequestTemplate));
        	Util.setSamlRequestTemplate(samlresponse);
        	
        	String samlPublicKey = samlConfDir+Util.getSAMLProperty(SAMLConstants.PUBLIC_KEY_CERTIFICATE);
        	byte[] publicKey = Util.getFileAsByte(samlPublicKey);
        	Util.setSamlPublicKey(publicKey);
        	
        	/*
        	 * Set exclude patter from Filter Config
        	 */
        	excludePattern = arg0.getInitParameter("excludePattern");
        	
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {if (samlConfigFile != null) {try {samlConfigFile.close();	} catch (IOException e) {e.printStackTrace();}}}
			
	}
	
}
