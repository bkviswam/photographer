package com.intuit.craft.photographer.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // Generate a unique request ID
            String requestId = UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);  // Add request ID to MDC

            chain.doFilter(request, response);  // Proceed with the request
        } finally {
            MDC.remove(REQUEST_ID);  // Clean up MDC after request completes
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}