package com.growthhub.salon.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("growthhub Salon Management API")
                .version("1.0.0")
                .description("""
                    REST API for growthhub Salon Management System.
                    
                    **Modules covered:**  
                    Auth · Clients · Staff · Appointments · Services · Inventory ·  
                    POS/Invoicing · Expenses · Attendance · Loyalty & Rewards ·  
                    Memberships · Gift Vouchers · Reports · Settings
                    
                    **Authentication:** Bearer JWT — use `/auth/login` to obtain a token,  
                    then pass it as `Authorization: Bearer <token>` on every request.
                    """)
                .contact(new Contact()
                    .name("growthhub Support")
                    .email("support@growthhub.com")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
