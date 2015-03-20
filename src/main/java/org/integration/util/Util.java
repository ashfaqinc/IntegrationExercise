package org.integration.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import org.integration.interfaces.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;
import net.oauth.signature.OAuthSignatureMethod;

public class Util {
	public static boolean verify(HttpServletRequest req, String key, String secret){
		OAuthMessage message_o=OAuthServlet.getMessage(req,null);

		//Construct an accessor and a consumer
		net.oauth.OAuthConsumer consumer=new net.oauth.OAuthConsumer(null, key, secret, null);
		OAuthAccessor accessor=new OAuthAccessor(consumer);

		//Now validate. Weirdly, validator has a void return type. It throws exceptions
		//if there are problems.
		try {
			OAuthSignatureMethod.newSigner(message_o, accessor).validate(message_o);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		
		return true;
	}
	
	public static String getReturnXml(String _message, String _success, String _accountIdentifier){
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();        
		DocumentBuilder icBuilder;
		
		try {            
			icBuilder = icFactory.newDocumentBuilder();            
			Document doc = icBuilder.newDocument();            
			Element result = doc.createElement("result");            
			doc.appendChild(result);
			
			Element success = doc.createElement("success");
			success.setTextContent(_success);
			result.appendChild(success);
			
			Element message = doc.createElement("message");
			message.setTextContent(_message);
			result.appendChild(message);
			
			Element accountIdentifier = doc.createElement("accountIdentifier");
			accountIdentifier.setTextContent(_accountIdentifier);
			result.appendChild(accountIdentifier);

			return convertDocumentToString(doc);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String getReturnXmlError(String _message, String _success, String _errorCode){
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();        
		DocumentBuilder icBuilder;
		
		try {            
			icBuilder = icFactory.newDocumentBuilder();            
			Document doc = icBuilder.newDocument();            
			Element result = doc.createElement("result");            
			doc.appendChild(result);
			
			Element success = doc.createElement("success");
			success.setTextContent(_success);
			result.appendChild(success);
			
			Element message = doc.createElement("message");
			message.setTextContent(_message);
			result.appendChild(message);
			
			Element errorCode = doc.createElement("errorCode");
			errorCode.setTextContent(_errorCode);
			result.appendChild(errorCode);

			return convertDocumentToString(doc);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String getUniqueIdentifier(){
		Random r = new Random();
		return "AshInc" + r.nextInt(2000);
	}
	
	public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
         
        return null;
    }
 
    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try 
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
    
    public static void addDataToResponse(String returnData, HttpServletResponse resp) throws IOException {
		resp.getWriter().write(returnData);		
	}

	public static String getDataFromEventURL(HttpServletRequest req, Request incomingRequest, String consumerKey, String consumerSecret) throws Exception {
		String eventURL = req.getParameter("url");
		
		if (eventURL == null){
			eventURL = incomingRequest.defaultGetURL();
		}
		
		
		String outputString = getDataFromUrl(eventURL, req, consumerKey, consumerSecret);
		System.out.println(outputString);
		
		return outputString;
	}
	
	public static String getDataFromUrl(String _url, HttpServletRequest req, String consumerKey, String consumerSecret) throws Exception{
		OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
		URL url = new URL(_url);
		URLConnection request = url.openConnection();
		consumer.sign(request);
		request.connect();
		
		BufferedInputStream in = new BufferedInputStream(request.getInputStream());
		
		if (in.available() > 0){
			Scanner scan = new Scanner(in, "UTF-8");
			String outputString = scan.useDelimiter("\\A").next();
			scan.close();
			
			return outputString;
		}
		return null;
	}

	public static boolean verifyRequest(HttpServletRequest req, String consumerKey, String consumerSecret) throws IOException {
		return Util.verify(req, consumerKey, consumerSecret);
	}
	
	public static String getBaseUrl(HttpServletRequest req){
		String url = req.getRequestURL().toString();
		String baseURL = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/";
		return baseURL;
	}
}
