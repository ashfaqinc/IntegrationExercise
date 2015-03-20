package org.integration.interfaces.impl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.integration.interfaces.Request;
import org.integration.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ChangeSubscriptionRequest implements Request {

	@Override
	public String action(String data) {
		System.out.println(data);
		
		//do something with the data i.e. perform the business rules to verify the account, verify if the account does not already have the subscription etc
		Document doc = Util.convertStringToDocument(data);
		
		//May be do some db insert / update (only if i had created the necessary columns
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList)xPath.evaluate("/event/payload/account/accountIdentifier",
			        doc.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
			return Util.getReturnXmlError("Account Successfully Failed", "false", "XPATH_ERROR");
		}

		Element e = (Element)nodes.item(0);
		String accId = e.getTextContent();
		System.out.println(accId);
		
		//return good response
		String message = Util.getReturnXml("Account Successfully Changed", "true", accId);
		
		System.out.println(accId);
		
		return message;
	}

	@Override
	public String defaultGetURL() {
		return "https://www.appdirect.com/AppDirect/rest/api/events/dummyChange";
	}

}
