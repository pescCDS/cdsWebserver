package org.pesc.cds.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Allows logging outgoing requests and the corresponding responses.
 * Requires the use of a {@link org.springframework.http.client.BufferingClientHttpRequestFactory} to log
 * the body of received responses.
 */
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    protected Logger requestLogger = LoggerFactory.getLogger("spring.web.client.MessageTracing.sent");
    protected Logger responseLogger = LoggerFactory.getLogger("spring.web.client.MessageTracing.received");


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(request, response);
        return response;
    }

    protected void logRequest(HttpRequest request, byte[] body) {
        if (requestLogger.isDebugEnabled()) {
            requestLogger.debug("===========================request begin================================================");
            requestLogger.debug("URI         : {}", request.getURI());
            requestLogger.debug("Method      : {}", request.getMethod());
            requestLogger.debug("Headers     : {}", request.getHeaders() );
            requestLogger.debug("==========================request end================================================");
        }
    }

    protected void logResponse(HttpRequest request, ClientHttpResponse response) {
        if (responseLogger.isDebugEnabled()) {
            try {
                responseLogger.debug("============================response begin==========================================");
                responseLogger.debug("Status code  : {}", response.getStatusCode());
                responseLogger.debug("Status text  : {}", response.getStatusText());
                responseLogger.debug("Headers      : {}", response.getHeaders());
                responseLogger.debug("=======================response end=================================================");
            } catch (IOException e) {
                responseLogger.warn("Failed to log response for {} request to {}", request.getMethod(), request.getURI(), e);
            }
        }
    }

}

