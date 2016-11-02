//$Id$
package com.zoho.oauth;



public class OAuthParams {
	
	private String accessToken;
	private String refreshToken;
	private Long expiryTime;
	
	public String getAccessToken() {
		return accessToken;
	}
	public OAuthParams setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public OAuthParams setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}
	public Long getExpiryTime() {
		return expiryTime;
	}
	public OAuthParams setExpiryTime( Long expiryTime) {
		this.expiryTime = expiryTime;
		return this;
	}
	public String toString()
	{
		return new StringBuilder(100)
		.append("ACCESS_TOKEN : ").append(accessToken).append(System.lineSeparator())
		.append("REFRESH_TOKEN : ").append(refreshToken).append(System.lineSeparator())
		.append("EXPIRY_TIME : ").append(expiryTime)
		.toString();
	}
}
