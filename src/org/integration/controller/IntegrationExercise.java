package org.integration.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.integration.interfaces.Request;
import org.integration.interfaces.impl.CancelSubscriptionRequest;
import org.integration.interfaces.impl.ChangeSubscriptionRequest;
import org.integration.interfaces.impl.CreateSubscriptionRequest;
import org.integration.openid.Consumer;
import org.integration.util.Util;
import org.openid4java.discovery.Identifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Controller
public class IntegrationExercise {
	
	private static Consumer consumer;
	
	static{
		try{
			consumer = new Consumer();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	//These values can be injected also
	private String consumerSecret="WXMlKejXAPopi77G";
	private String consumerKey="integration-challange-19566";
	
	//Different Request handlers containing business rules
	//If we want to integrate more app direct endpoints, we have to
	//	1. specify the endpoint url-pattern in web.xml
	//	2. create a class that implements org.integration.interfaces.Request and implements
	//	the methods of the interface with business logic
	//	3. create a method in this class that will handle the url and call the algo method
	//	passing in the implemented class to handle the request
	private Request createSubscriptionRequest = new CreateSubscriptionRequest();
	private Request changeSubscriptionRequest = new ChangeSubscriptionRequest();
	private Request cancelSubscriptionRequest = new CancelSubscriptionRequest();
	
	@RequestMapping("/openid")
	public void openid(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		System.out.println(req.getRequestURL());
		String openIDUrl = req.getParameter("openid_url");	
		consumer.authRequest(openIDUrl, req, resp);
	}
	
	@RequestMapping("/openidresponse")
	public void openidresponse(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Identifier id = consumer.verifyResponse(req);
		//Once verified, send them to your application. In my case, I am redirecting them
		//to the subscriptions page
		if (id != null)
			resp.sendRedirect("http://198.48.197.204:8080/IntegrationExercise/subscriptions");
		else
			throw new Exception("Not Verified"); //Do something if not verified. I am throwing an exception. Its just pure lazy but meh !!! I do this throughout the app though :)
	}
	
	@RequestMapping("/subscriptions")
	public ModelAndView subscriptions(HttpServletRequest req) throws Exception {
		//I am getting 401 error when I try to get the data using the get url, so if this is somehow fixed, I should be able to display the page nicely
		String url = "https://www.appdirect.com/api/billing/v1/companies/4fff2acf-0ce5-47a2-949e-6df583ddfef4/subscriptions";		
		String data = Util.getDataFromUrl(url, req, consumerKey, consumerSecret);
		
		System.out.println(data);
		
		Document doc = Util.convertStringToDocument(data);
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList)xPath.evaluate("/subscriptions/subscription[status='FREE_TRIAL']/externalAccountId",
		        doc.getDocumentElement(), XPathConstants.NODESET);
		
		StringBuilder htmlData = new StringBuilder("<table style=\"width:100%\"><tr><th>AccountName</th></tr>");
		
		for (int i = 0; i < nodes.getLength(); ++i) {
		    Element e = (Element) nodes.item(i);
		    htmlData.append("<tr><td>");
		    htmlData.append(e.getTextContent());
		    htmlData.append("</tr></td>");
		}
		
		System.out.println(htmlData.toString());
		
		String message = "<br><div align='center'>"
				+ "<h3>List of Subscribed Users</h3>" + htmlData.toString() + "<br><br>";
		
		/*String message = "<br><div align='center'>"
				+ "<h3>List of Subscribed Users</h3>Ashfaq<br><br>";*/
		
		return new ModelAndView("showSubscriptions", "message", message);
	}
	
	@RequestMapping("/subscribeCreate")
	public void subscribeCreate(HttpServletRequest req, HttpServletResponse resp) throws Exception {		
		algo(req, resp, createSubscriptionRequest);		
	}
	
	@RequestMapping("/subscribeChange")
	public void subscribeChange(HttpServletRequest req, HttpServletResponse resp) throws Exception {		
		algo(req, resp, changeSubscriptionRequest);		
	}
	
	@RequestMapping("/subscribeCancel")
	public void subscribeCancel(HttpServletRequest req, HttpServletResponse resp) throws Exception {		
		algo(req, resp, cancelSubscriptionRequest);		
	}
	
	private void algo (HttpServletRequest req, HttpServletResponse resp, Request incomingRequest) throws Exception{		
		if(Util.verifyRequest(req, consumerKey, consumerSecret)){
			String data = Util.getDataFromEventURL(req, incomingRequest, consumerKey, consumerSecret);
			String returnData = Util.performRequestedAction(incomingRequest, data);
			Util.addDataToResponse(returnData, resp);
			resp.setStatus(201);
			resp.setContentType("text/xml;charset=UTF-8");
		}
		else{
			resp.setStatus(201);
			resp.getWriter().write(Util.getReturnXmlError("Incoming message could not be verified", "false", "ERROR"));
		}
	}

	
}
