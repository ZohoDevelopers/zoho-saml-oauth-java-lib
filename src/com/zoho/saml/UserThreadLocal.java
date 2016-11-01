package com.zoho.saml;


class UserThreadLocal {
	static final ThreadLocal<User> tl = new ThreadLocal<User>();
	static void set(User user)
	{
		tl.set(user);
	}
	static User get()
	{
		return tl.get();
	}
	static void cleanUp()
	{
		tl.remove();
	}
}
