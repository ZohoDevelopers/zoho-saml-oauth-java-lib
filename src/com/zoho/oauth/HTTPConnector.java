//$Id$
package com.zoho.oauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
public class HTTPConnector {
	private String url;
	private HashMap<String,String> requestParams = new HashMap<String, String>();
	private HashMap<String,String> requestHeaders = new HashMap<String, String>();
   
	public String post() throws Exception
	{
		final String USER_AGENT = "Mozilla/5.0";

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		/*
		 * Set request Parameters
		 */
		if(requestParams!=null&&!requestParams.isEmpty())
		{
			for(String key: requestParams.keySet())
			{
				String value=requestParams.get(key);
				if(value!=null)
				{
					urlParameters.add(new BasicNameValuePair(key, value));
				}
			}
		}
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		/*
		 * Set request Headder
		 */
		if(requestHeaders!=null && !requestHeaders.isEmpty())
		{
			for(String key: requestHeaders.keySet())
			{
				String value=requestHeaders.get(key);
				if(value!=null)
				{
					post.setHeader(key, value);
				}
			}
		}
		HttpResponse response = client.execute(post);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
	/*
	 * getter setters
	 */
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void addParam(String key,String value) {
		requestParams.put(key, value);

	}
	public void addHeadder(String key,String value) {
		requestHeaders.put(key, value);
	}
}
	
