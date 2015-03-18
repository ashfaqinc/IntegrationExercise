package org.integration.openid;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.*;
import org.openid4java.OpenIDException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.io.IOException;

public class Consumer {
	public ConsumerManager manager;

    public Consumer() throws ConsumerException
    {
        // instantiate a ConsumerManager object
        manager = new ConsumerManager();
    }

    // --- placing the authentication request ---
    public String authRequest(String userSuppliedString,
                              HttpServletRequest httpReq,
                              HttpServletResponse httpResp)
            throws IOException
    {
        try
        {
            String returnToUrl = "http://198.48.197.204:8080/IntegrationExercise/openidresponse";

            // perform discovery on the user-supplied identifier
            List discoveries = manager.discover(userSuppliedString);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);

            // store the discovery information in the user's session
            httpReq.getSession().setAttribute("openid-disc", discovered);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);
            httpResp.sendRedirect(authReq.getDestinationUrl(true));
        }
        catch (OpenIDException e)
        {
            e.printStackTrace();
            return "Error in OpenID Authentication";
        }

        return null;
    }

    // --- processing the authentication response ---
    public Identifier verifyResponse(HttpServletRequest httpReq)
    {
        try
        {
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response =
                    new ParameterList(httpReq.getParameterMap());

            // retrieve the previously stored discovery information
            DiscoveryInformation discovered = (DiscoveryInformation)
                    httpReq.getSession().getAttribute("openid-disc");

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = httpReq.getRequestURL();
            String queryString = httpReq.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(httpReq.getQueryString());

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(
                    receivingURL.toString(),
                    response, discovered);

            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null)
            {
                return verified;  // success
            }
        }
        catch (OpenIDException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
