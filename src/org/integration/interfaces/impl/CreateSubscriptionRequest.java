package org.integration.interfaces.impl;

import org.integration.interfaces.Request;
import org.integration.util.Util;
import org.w3c.dom.Document;

public class CreateSubscriptionRequest implements Request {

	@Override
	public String action(String data) {
		System.out.println(data);
		
		//do something with the data i.e. perform the business rules to create the account, verify if the account does not already exists etc
		Document doc = Util.convertStringToDocument(data);
		
		//return good response
		String accId = Util.getUniqueIdentifier();
		String message = Util.getReturnXml("Account Successfully Created", "true", accId);
		
		System.out.println(accId);
		
		return message;
	}

	@Override
	public String defaultGetURL() {
		// TODO Auto-generated method stub
		return "https://www.appdirect.com/api/integration/v1/events/dummyOrder";
	}

}
