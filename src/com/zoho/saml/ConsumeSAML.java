package com.zoho.saml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
public class ConsumeSAML extends HttpServlet {
	
	public void service(HttpServletRequest req, HttpServletResponse res){
		try {
				String SAMLResponse = req.getParameter("SAMLResponse");
		    	if(SAMLResponse==null)
		    	{
		    		throw new  Exception("SAML RESPONSE MISSING");
		    	}
		    	Document samlResponseDoc = null;
		    	try
		        {
		    		String decodedSAMLResponse = decodeSamlResponse(SAMLResponse);
		    		samlResponseDoc = constructDocument(decodedSAMLResponse);
		        }catch(Exception e)
		        {
		        	throw new  Exception("SAML RESPONSE MISSING");
		        }
			        /*
			         * See if the signature is valid
			         */
			        NodeList nl = samlResponseDoc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			        if(nl.getLength()==0){
			        	throw new  Exception("SAML Signature Missing");
			        }
			        /*
			         * get Signature from samlResponse
			         */
			        Node signatureTag = nl.item(0);
			        
			        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", java.security.Security.getProvider("XMLDSig"));
			        PublicKey validationKey = getPublicKey();
			        DOMValidateContext valContext = new DOMValidateContext(validationKey,signatureTag);

			        /*
			         * Validate The signature and the content received through saml response
			         */
			        valContext.setIdAttributeNS((Element) samlResponseDoc.getElementsByTagName("Assertion").item(0), null, "ID");
			        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
					Boolean coreValidity = signature.validate(valContext);
					
					/*
					 * if the signature is valid proceed with the user credentials provided in saml response
					 */
			         if(coreValidity)
			         {
			        	 NodeList nameidTags = samlResponseDoc.getElementsByTagName("NameID");
			        	 if(nameidTags==null)
			        	 {
			        		 throw new Exception("NAME-ID tag missing from SamlResponse");
			        	 }
			        	 Node nameID= nameidTags.item(0);
			        	 /*
			        	  * fetch UserInformation from SAML Response
			        	  */
			        	 String userEmail = nameID.getTextContent().trim();
			        	 Element attributeStateMent = (Element) samlResponseDoc.getElementsByTagName("AttributeStatement").item(0);
			        	 JSONObject userDetails = new JSONObject();
			        	 if(attributeStateMent!=null && attributeStateMent.hasChildNodes())
			        	 {
			        		 NodeList attributes = attributeStateMent.getChildNodes();
			        		 for(int i=0;i<attributes.getLength();i++)
			        		 {
			        			 Node temp = attributes.item(i);
			        			 if(temp.getNodeType() ==  Node.ELEMENT_NODE)
			        			 {
			        				 Element attribute = (Element) temp;
			        				 String name = attribute.getAttribute("Name");
			        				 String value = attribute.getChildNodes().item(0).getTextContent();
			        				 userDetails.put(name, value);
			        			 }
			        		 }
			        	 }
			        	 User userinfo  =  new User(userEmail, userDetails);
			        	 /*
			        	  * Set userInfo into session
			        	  */
			        	 if(userinfo!=null)
			        	 {
			        		 req.getSession().setAttribute(SAMLConstants.USER_IN_SESSION, userinfo);
			        	 }
			        	 /*
			        	  * Redirect User To HomePage
			        	  */
						 String homePageURL = Util.getSAMLProperty(SAMLConstants.HOME_PAGE);
			        	 res.sendRedirect(homePageURL);
			        	 return;
			         }
			         else{
			        	 //Invalid response
				           throw new  Exception("Invalid SAML Signature Detected");
			         }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String decodeSamlResponse(String encodedResponse)
	{
		 byte[] decodedBytes =  Base64.decodeBase64(encodedResponse);
         StringBuilder stringBuffer = new StringBuilder();
		for (byte c : decodedBytes) 
		{
				stringBuffer.append((char) c);
		}
		return stringBuffer.toString();
	}
	private static Document constructDocument(String xml) throws Exception
	 {
	     DocumentBuilderFactory factory = DocumentBuilderFactory
	         .newInstance();
	     factory.setNamespaceAware(true);
	     
	     return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
	 }
	private static PublicKey getPublicKey() 
	{
		try {
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			String samlPublicKey = Util.getSamlConfDir()+Util.getSAMLProperty(SAMLConstants.PUBLIC_KEY_CERTIFICATE);
			FileInputStream is = new FileInputStream (samlPublicKey);
			X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
			PublicKey key = cer.getPublicKey();
			is.close();
			return key;
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
}
