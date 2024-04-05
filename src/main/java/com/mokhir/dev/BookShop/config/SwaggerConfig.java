package com.mokhir.dev.BookShop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Token";
        final String apiTitle = String.format(StringUtils.capitalize("Internship project REST API"));
        final String[] developers = {"Azodov Doniyor"};
        final String apiDescription = String.format("""
                        Ushbu REST API %s uchun yaratilgan.\s

                        Dasturchi: %s
                        """,
                apiTitle,
                Arrays.toString(developers)
        );
        return new OpenAPI()
                .addServersItem(new Server().url("/api").description("Root API server"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .description("JSON Web Token")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title(apiTitle)
                        .version("1.0")
                        .description(apiDescription)
                        .summary(apiDescription)
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)
                );
    }

}
