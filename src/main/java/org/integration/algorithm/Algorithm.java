package org.integration.algorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.integration.types.RequestType;

public interface Algorithm {
	public void algo(HttpServletRequest req, HttpServletResponse resp, RequestType type) throws Exception;
}
