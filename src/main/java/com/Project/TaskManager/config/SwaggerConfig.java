package com.Project.TaskManager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI taskFlowOpenAPI() {
        return new OpenAPI()
                // ─── API Info ─────────────────────────────────────────────
                .info(new Info()
                        .title("TaskFlow API")
                        .description("""
                                TaskFlow — Production grade project management backend.
                                
                                Built with Spring Boot 4.x + Java 25 + PostgreSQL + Redis + RabbitMQ + WebSockets + Google Gemini AI.
                                
                                All endpoints except /auth/** require a Bearer JWT token.
                                Use POST /api/v1/auth/login to get your token, then click Authorize.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sonu Verma")
                                .email("sonu@taskflow.com"))
                        .license(new License()
                                .name("MIT License")))

                // ─── JWT Security Scheme ──────────────────────────────────
                // Adds the Authorize button to Swagger UI
                // Once you enter your JWT token, all requests include it
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token here. Get it from POST /api/v1/auth/login")));
    }
}