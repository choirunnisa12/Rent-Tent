package com.code.camping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public HeaderWriter securityHeadersWriter() {
        return new StaticHeadersWriter("X-Content-Type-Options", "nosniff");
    }
} 