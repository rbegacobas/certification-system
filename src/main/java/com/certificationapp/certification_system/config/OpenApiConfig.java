package com.certificationapp.certification_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Certification System API")
                        .version("1.0")
                        .description("API for managing professional certifications")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("team@certificationapp.com")));
    }
}