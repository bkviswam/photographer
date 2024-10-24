package com.intuit.craft.photographer.filter;

import com.intuit.craft.photographer.util.DatabaseTimeTracker;
import com.intuit.craft.photographer.util.RedisCacheTimeTracker;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final String REQUEST_ID = "requestId";

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request,
                         jakarta.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate and log a unique request ID
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);

        long requestStartTime = System.currentTimeMillis();

        // Start the request and chain the filters
        RedisCacheTimeTracker.clear();
        DatabaseTimeTracker.clear();

        try {
            chain.doFilter(request, response);
        } finally {
            // Measure the total request time and server time
            long totalRequestTime = System.currentTimeMillis() - requestStartTime;
            long databaseTime = DatabaseTimeTracker.getDatabaseTime();
            long cacheTime = RedisCacheTimeTracker.getCacheTime();
            long serverProcessingTime = totalRequestTime - databaseTime - cacheTime;

            // Log the times and request details
            logger.info("uuid={}, method={}, uri={}, code={}, sst={} ms, dbt={} ms, cct={} ms, tt={} ms",
                    requestId, httpRequest.getMethod(), httpRequest.getRequestURI(),
                    httpResponse.getStatus(), serverProcessingTime, databaseTime, cacheTime, totalRequestTime);

            // Clear the ThreadLocal storage to avoid memory leaks
            DatabaseTimeTracker.clear();
            RedisCacheTimeTracker.clear();
            MDC.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
