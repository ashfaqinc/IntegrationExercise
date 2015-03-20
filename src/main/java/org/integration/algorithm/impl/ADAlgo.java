package org.integration.algorithm.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.integration.algorithm.Algorithm;
import org.integration.interfaces.Request;
import org.integration.interfaces.impl.CancelSubscriptionRequest;
import org.integration.interfaces.impl.ChangeSubscriptionRequest;
import org.integration.interfaces.impl.CreateSubscriptionRequest;
import org.integration.types.RequestType;
import org.integration.util.Util;

public class ADAlgo implements Algorithm {
	
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
		
	public void algo (HttpServletRequest req, HttpServletResponse resp, RequestType _incomingRequest) throws Exception{
		
		Request incomingRequest = getRequestFromType(_incomingRequest);
		
		if(Util.verifyRequest(req, consumerKey, consumerSecret)){
			String data = Util.getDataFromEventURL(req, incomingRequest, consumerKey, consumerSecret);
			String returnData = performRequestedAction(incomingRequest, data);
			Util.addDataToResponse(returnData, resp);
			resp.setStatus(201);
			resp.setContentType("text/xml;charset=UTF-8");
		}
		else{
			resp.setStatus(201);
			resp.getWriter().write(Util.getReturnXmlError("Incoming message could not be verified", "false", "ERROR"));
		}
	}
	
	

	private String performRequestedAction(Request incomingRequest, String data) {
		return incomingRequest.action(data);
	}



	private Request getRequestFromType(RequestType _incomingRequest) {
		if (_incomingRequest == RequestType.SUBSCRIPTION_CREATE)
			return createSubscriptionRequest;
		
		else if (_incomingRequest == RequestType.SUBSCRIPTION_CHANGE)
			return changeSubscriptionRequest;
		
		else if (_incomingRequest == RequestType.SUBSCRIPTION_CANCEL)
			return cancelSubscriptionRequest;
		
		return null;
	}
}
