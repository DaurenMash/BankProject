package com.bank.authorization.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Authorization API")
                        .version("1.0")
                        .description("API for Bank Authorization"));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.getPaths().keySet().forEach(path -> {
            String newPath = "/api/authorization" + path;
            openApi.getPaths().addPathItem(newPath, openApi.getPaths().remove(path));
        });
    }
}