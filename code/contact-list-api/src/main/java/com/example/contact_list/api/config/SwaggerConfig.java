package com.example.contact_list.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Contact List API")
                        .version("1.0")
                        .description("API for managing the contact list"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local")));
    }
}
