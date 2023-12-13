package com.example.shopuserservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class SwaggerConfig {
    private final String springdocVersion = "5.2.x";

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}")String springdocVersion ){
        Contact contact = new Contact()
            .email("ghkdqhrbals@gmail.com")
            .url("https://ghkdqhrbals.github.io/portfolios")
            .name("Hwangbo Gyumin");

        License license = new License().name("License of API")
            .url("https://github.com/ghkdqhrbals/spring-chatting-server/blob/main/LICENSE");

        Info info = new Info()
            .title("User Service API")
            .description("User Service API Specification")
            .version(springdocVersion)
            .contact(contact)
            .license(license);

        return new OpenAPI().info(info);
    }
}
