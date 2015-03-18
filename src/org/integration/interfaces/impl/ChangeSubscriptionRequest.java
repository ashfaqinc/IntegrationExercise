package org.integration.interfaces.impl;

import org.integration.interfaces.Request;
import org.integration.util.Util;
import org.w3c.dom.Document;

public class ChangeSubscriptionRequest implements Request {

	@Override
	public String action(String data) {
		System.out.println(data);
		
		//do something with the data i.e. perform the business rules to verify the account, verify if the account does not already have the subscription etc
		Document doc = Util.convertStringToDocument(data);
		
		//return good response
		String accId = Util.getUniqueIdentifier();
		String message = Util.getReturnXml("Account Successfully Changed", "true", accId);
		
		System.out.println(accId);
		
		return message;
	}

	@Override
	public String defaultGetURL() {
		return "https://www.appdirect.com/AppDirect/rest/api/events/dummyChange";
	}

}
