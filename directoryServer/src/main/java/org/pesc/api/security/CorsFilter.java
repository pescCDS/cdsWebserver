package org.pesc.api.security;

/**
 * Created by james on 8/8/16.
 */

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        if (request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null && HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            // CORS "pre-flight" request
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
            response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_TYPE + ", " + StatelessCSRFFilter.X_CSRF_TOKEN_HEADER);
            response.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1");
        }

        filterChain.doFilter(request, response);
    }

}