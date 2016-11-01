package com.zoho.saml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

class Util {

	private static Properties samlProp = null;

	private static String samlRequestTemplate = null;
	private static byte[] samlPublicKey = null;
	private static String samlConfDir;
	public static String getSAMLProperty(String propName) {
		if (getSamlProp() != null) {
			return getSamlProp().getProperty(propName);
		} else {
			return null;
		}
	}
	public static String getFileAsString(File file)	throws FileNotFoundException, IOException 
	{
		StringWriter content = new StringWriter();
		int readCount = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")); // No I18N
			char[] buf = new char[8192];
			while ((readCount = reader.read(buf)) > 0) {
				content.write(buf, 0, readCount);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (content != null) {
				content.close();
			}
		}
		return content.toString();
	}
	public static  byte[] getFileAsByte(String filePath) 
	{
		File file = new File(filePath);
		byte[] buffer = new byte[(int) file.length()];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(buffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buffer;
	}
	public static String getSamlRequestTemplate() {
		return samlRequestTemplate;
	}

	public static void setSamlRequestTemplate(String samlRequestTemplate) {
		Util.samlRequestTemplate = samlRequestTemplate;
	}

	public static byte[] getSamlPublicKey() {
		return samlPublicKey;
	}

	public static void setSamlPublicKey(byte[] samlPublicKey) {
		Util.samlPublicKey = samlPublicKey;
	}

	public static Properties getSamlProp() {
		return samlProp;
	}

	public static void setSamlProp(Properties samlProp) {
		Util.samlProp = samlProp;
	}
	public static String getSamlConfDir() {
		return samlConfDir;
	}
	public static void setSamlConfDir(String samlConfDir) {
		Util.samlConfDir = samlConfDir;
	}
	public static void redirectToLoginPage(HttpServletResponse response) throws IOException
	{
		 String loginPageURL = Util.getSAMLProperty(SAMLConstants.LOGIN_PAGE);
		 response.sendRedirect(loginPageURL);
	}
}
