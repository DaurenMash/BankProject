package com.bank.history.config;

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
                        .title("History Service API")
                        .version("1.0")
                        .description("Документация API сервиса истории"))
                .servers(List.of(new Server().url("http://localhost:8088").description("Локальный сервер")));
    }
}

