package com.pucpr.casetecnico.backend.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Case Técnico - PUCPR")
                        .description("Documentação da API do backend responsável por usuários, espaços, presença e dashboards.")
                        .version("v1.0.0")
                        .contact(new Contact().name("PUCPR"))
                        .license(new License().name("Uso acadêmico")));
    }
}

