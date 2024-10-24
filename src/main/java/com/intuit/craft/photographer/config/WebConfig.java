package com.intuit.craft.photographer.config;

import com.intuit.craft.photographer.filter.RequestIdFilter;
import com.intuit.craft.photographer.filter.RequestResponseLoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public Filter requestIdFilter() {
        return new RequestIdFilter();
    }

    @Bean
    public Filter requestResponseLoggingFilter() {
        return new RequestResponseLoggingFilter();
    }
}

