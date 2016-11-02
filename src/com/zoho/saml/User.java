package com.zoho.saml;

import java.util.Iterator;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private String name;
   	private String email;
   	
	private Long userID;
	private String lang;
	private String country;
	private Locale locale;
	
	private String timeZone;
	private Long orgID;
	private Boolean isAdmin;
   	
	public User()
	{
		
	}
	public User(String email , JSONObject userDetails) throws JSONException
	{
		this.email = email;
		Iterator<String> keys = userDetails.keys();
		while(keys.hasNext())
		{
			String prop = keys.next();
			String value =(String) userDetails.getString(prop);
			if("ORG_ID".equals(prop))
			{
				this.orgID = Long.parseLong(value);
			}
			else if("USER_ID".equals(prop))
			{
				this.userID = Long.parseLong(value);
			}
			else if("DISPLAY_NAME".equals(prop))
			{
				this.name = value;
			}
			else if("TIME_ZONE".equals(prop))
			{
				this.timeZone = value;
			}
			else if("IS_ORG_ADMIN".equals(prop))
			{
				this.isAdmin =Boolean.parseBoolean(value);
			}
			else if("LANGUAGE".equals(prop))
			{
				this.lang =value;
			}
			else if("COUNTRY".equals(prop))
			{
				this.country =value;
			}
		}
		locale = new Locale(this.lang, this.country);
	}
	public String getEmail() {
		return email;
	}
	public User setEmail(String email) {
		this.email = email;
		return this;
	}
	public String getName() {
		return name;
	}
	public User setName(String name) {
		this.name = name;
		return this;
	}
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Long getOrgID() {
		return orgID;
	}
	public void setOrgID(Long orgID) {
		this.orgID = orgID;
	}
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String toString()
	{
		StringBuilder sb = new StringBuilder(200);
		sb
		.append("Name:").append(this.name).append("\n")
		.append("Email:").append(this.email).append("\n")
		.append("UserID:").append(this.userID).append("\n")
		.append("Locale:").append(this.locale).append("\n")
		.append("TimeZone:").append(this.timeZone).append("\n")
		.append("OrgID:").append(this.orgID).append("\n")
		.append("IsAdmin:").append(this.isAdmin);
		return sb.toString();
	}
}
