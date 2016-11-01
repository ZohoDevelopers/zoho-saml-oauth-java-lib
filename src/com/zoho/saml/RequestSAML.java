package com.zoho.saml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

public class RequestSAML extends HttpServlet {
	
	public void service(HttpServletRequest req, HttpServletResponse res)throws IOException 
	{
		/*
		 * Initiate SAML Request to IDP
		 */
		try {
			/*
			 * Code to generate Saml request and redirect to the SSO url
			 */
			String samlRequest = new String(Util.getSamlRequestTemplate());

			/*
			 * ACSURL  : - url to redirect samlResponse to 
			 */
			String acsURL = Util.getSAMLProperty(SAMLConstants.ASSERTION_URL);
			samlRequest = samlRequest.replaceAll("--ACSURL--", acsURL); // No I18N
			
			/*
			 * ID : Unique ID per samlRequest 
			 */
			
    		String authToken = UUID.randomUUID().toString();
    		samlRequest = samlRequest.replaceAll("--ID--", authToken); // No I18N

    		/*
			 * Destination : - SamlSSO URL
			 */
    		String samlSignInURL = Util.getSAMLProperty(SAMLConstants.SAML_SSO_URL);
			samlRequest = samlRequest.replaceAll("--Destination--", samlSignInURL); 
    		
    		/*
			 * ServiceProvider Name : - To Identify the serviceProvider in IDP's end
			 */
			samlRequest = samlRequest.replaceAll("--ProviderName--", "VerticalCRM"); 

			
    		String ssoURL = Util.getSAMLProperty(SAMLConstants.SAML_SSO_URL);
    		samlRequest = encodeSAMLRequest(samlRequest);
    		samlRequest = URLEncoder.encode(samlRequest, "UTF-8"); // No I18N
    	
    		if (ssoURL.indexOf('?') == -1) {
    			ssoURL=ssoURL + "?SAMLRequest=" + samlRequest; // No I18N
    		} else {
    			ssoURL=ssoURL + "&SAMLRequest=" + samlRequest; // No I18N
    		}
    		res.sendRedirect(ssoURL);
    		return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String encodeSAMLRequest(String samlRequest) throws IOException  
	{
		if (samlRequest == null || "".equals(samlRequest)) {
			return samlRequest;
		}
		ByteArrayOutputStream bos = null;
		DeflaterOutputStream dos = null;
		try {
			Deflater deflater = new Deflater(Deflater.DEFLATED, true);
			byte[] b = samlRequest.getBytes("UTF-8"); // No I18N
			bos = new ByteArrayOutputStream();
			dos = new DeflaterOutputStream(bos, deflater);
			dos.write(b, 0, b.length);
			dos.finish();
			String encMessage =BASE64_ENCODE(bos.toByteArray());
			return encMessage;
		} finally {
			if (bos != null) 
			{
				try {
					bos.close();
				} catch (Exception e) {
				}
			}
			if (dos != null) 
			{
				try {
					dos.close();
			} catch (Exception e) {
			}
		}
		}
	}
	 public static String BASE64_ENCODE(byte[] binaryData) {
			return BASE64_ENCODE(binaryData, false);
		}
	 public static String BASE64_ENCODE(byte[] binaryData, boolean replaceNewLine) {
			String cp = Base64.encodeBase64String(binaryData);
			if (!replaceNewLine) {
				return cp;
			}
			if (cp.endsWith("\n")) {
				cp = cp.substring(0, cp.length() - 1);
			}
			StringBuilder toReturn = new StringBuilder();
			for (int i = 0; i < cp.length(); i++) {
				String t = cp.charAt(i) + "";
				if (t.hashCode() != 13) {
					toReturn.append(t);
				}
			}
			return toReturn.toString();
		}
}
