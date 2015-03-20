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

public class CancelSubscriptionRequest implements Request {

	private PersistData pd = PersistData.getPersistDataInstance();
	@Override
	public String action(String data) {
		System.out.println(data);
		
		//do something with the data i.e. perform the business rules to verify the account, cancel it if account successfully settled etc
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
			pd.delete(name);
		} catch (Exception e1) {
			e1.printStackTrace();
			return Util.getReturnXmlError("Account Successfully Failed", "false", "DB_INSERT_ERROR");
		}
		
		//return good response
		xPath = XPathFactory.newInstance().newXPath();
		
		try {
			nodes = (NodeList)xPath.evaluate("/event/payload/account/accountIdentifier",
			        doc.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
			return Util.getReturnXmlError("Account Successfully Failed", "false", "XPATH_ERROR");
		}

		e = (Element)nodes.item(0);
		String accId = e.getTextContent();
		System.out.println(accId);
		message = Util.getReturnXml("Account Successfully Cancelled", "true", accId);
		
		System.out.println(accId);
		
		return message;
	}

	@Override
	public String defaultGetURL() {
		return "https://www.appdirect.com/AppDirect/rest/api/events/dummyCancel";
	}

}
