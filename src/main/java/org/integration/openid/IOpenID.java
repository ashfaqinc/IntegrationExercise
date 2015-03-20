package org.integration.openid;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.discovery.Identifier;

public interface IOpenID {
	public String authRequest(String userSuppliedString, HttpServletRequest httpReq, HttpServletResponse httpResp, String returnToUrl) throws IOException;
	public Identifier verifyResponse(HttpServletRequest httpReq);	
}
