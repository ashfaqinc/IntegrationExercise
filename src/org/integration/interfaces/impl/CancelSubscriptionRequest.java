package org.integration.interfaces.impl;

import org.integration.interfaces.Request;
import org.integration.util.Util;
import org.w3c.dom.Document;

public class CancelSubscriptionRequest implements Request {

	@Override
	public String action(String data) {
		System.out.println(data);
		
		//do something with the data i.e. perform the business rules to verify the account, cancel it if account successfully settled etc
		Document doc = Util.convertStringToDocument(data);
		
		//return good response
		String accId = Util.getUniqueIdentifier();
		String message = Util.getReturnXml("Account Successfully Cancelled", "true", accId);
		
		System.out.println(accId);
		
		return message;
	}

	@Override
	public String defaultGetURL() {
		return "https://www.appdirect.com/AppDirect/rest/api/events/dummyCancel";
	}

}
