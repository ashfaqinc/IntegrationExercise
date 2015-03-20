package org.integration.interfaces.impl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.integration.interfaces.Request;
import org.integration.persistance.impl.PersistData;
import org.integration.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CreateSubscriptionRequest implements Request {
	private PersistData pd = PersistData.getPersistDataInstance();
	
	@Override
	public String action(String data) {
		System.out.println(data);
		
		//do something with the data i.e. perform the business rules to create the account, verify if the account does not already exists etc
		Document doc = Util.convertStringToDocument(data);
		String message = "";
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList)xPath.evaluate("/event/creator/firstName",
			        doc.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
			return Util.getReturnXmlError("Account Successfully Failed", "false", "XPATH_ERROR");
		}

		Element e = (Element)nodes.item(0);
		String name = e.getTextContent();
		System.out.println(name);
		
		try {
			pd.insert(name);
		} catch (Exception e1) {
			e1.printStackTrace();
			return Util.getReturnXmlError("Account Successfully Failed", "false", "DB_INSERT_ERROR");
		}
		
		//return good response
		String accId = Util.getUniqueIdentifier();
		message = Util.getReturnXml("Account Successfully Created", "true", accId);
		
		System.out.println(accId);
		
		return message;
	}

	@Override
	public String defaultGetURL() {
		// TODO Auto-generated method stub
		return "https://www.appdirect.com/api/integration/v1/events/dummyOrder";
	}

}
