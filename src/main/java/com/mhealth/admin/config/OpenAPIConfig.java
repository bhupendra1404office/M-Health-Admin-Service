package com.mhealth.admin.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("M-Health Admin Portal")
                .version("v1")
                .description("API Documentation for Admin portal"))
                .externalDocs(new ExternalDocumentation()
                        .description("Learn more"));
    }
}