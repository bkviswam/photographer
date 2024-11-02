package com.algo.dev.photographer.config;

import com.algo.dev.photographer.filter.RequestIdFilter;
import com.algo.dev.photographer.filter.RequestResponseLoggingFilter;
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

