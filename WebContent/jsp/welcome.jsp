<%@page import="com.zoho.saml.SamlUtil"%>
<%@page import="com.zoho.saml.User"%>
<%
User user=SamlUtil.getCurrentUser();
%>
Welcome <span style="font-weight: bold;"><%=user%></span>
<span style="float: right;"><a href="/zoho/logout">Sign Out</a></span>