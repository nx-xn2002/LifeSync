package com.nx.lifesyncbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc config
 *
 * @author nx-xn2002
 * @date 2025-01-18
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .maxAge(3600)
                .allowedHeaders("Origin", "Accept", "Content-Type", "Authorization")
                .allowCredentials(true);
    }
}

