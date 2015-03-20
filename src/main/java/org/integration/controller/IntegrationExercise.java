package org.integration.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.integration.algorithm.Algorithm;
import org.integration.algorithm.impl.ADAlgo;
import org.integration.openid.IOpenID;
import org.integration.openid.impl.OpenIDAuthenticator;
import org.integration.persistance.IPersist;
import org.integration.persistance.impl.PersistData;
import org.integration.types.RequestType;
import org.integration.util.Util;
import org.openid4java.discovery.Identifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IntegrationExercise {
	
	private IOpenID consumer;
	private IPersist persist = PersistData.getPersistDataInstance();
	private Algorithm algo = new ADAlgo();
	
	public IntegrationExercise(){
		try{
			consumer = new OpenIDAuthenticator();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@RequestMapping("/openid")
	public void openid(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		//System.out.println(req.getRequestURL());
		String openIDUrl = req.getParameter("openid_url");
		//System.out.println(req.getContextPath());
		String returnToUrl = Util.getBaseUrl(req) + "openidresponse";
		consumer.authRequest(openIDUrl, req, resp, returnToUrl);
	}
	
	@RequestMapping("/openidresponse")
	public void openidresponse(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Identifier id = consumer.verifyResponse(req);
		String returnUrl = Util.getBaseUrl(req) + "subscriptions";
		//Once verified, send them to your application. In my case, I am redirecting them
		//to the subscriptions page
		if (id != null)
			resp.sendRedirect(returnUrl);
		else
			throw new Exception("Not Verified"); //Do something if not verified. I am throwing an exception. Its just pure lazy but meh !!! I do this throughout the app though :)
	}
	
	@RequestMapping("/subscriptions")
	public ModelAndView subscriptions(HttpServletRequest req) throws Exception {
		List<String> users = persist.getUsers();
		
		StringBuilder htmlData = new StringBuilder("<table style=\"width:100%\"><tr><th>AccountName</th></tr>");
		
		for (String user: users) {
		    htmlData.append("<tr><td align='center'>");
		    htmlData.append(user);
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
		algo.algo(req, resp, RequestType.SUBSCRIPTION_CREATE);		
	}
	
	@RequestMapping("/subscribeChange")
	public void subscribeChange(HttpServletRequest req, HttpServletResponse resp) throws Exception {		
		algo.algo(req, resp, RequestType.SUBSCRIPTION_CHANGE);		
	}
	
	@RequestMapping("/subscribeCancel")
	public void subscribeCancel(HttpServletRequest req, HttpServletResponse resp) throws Exception {		
		algo.algo(req, resp, RequestType.SUBSCRIPTION_CANCEL);		
	}
	
	

	
}
