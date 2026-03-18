package com.anyview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AnyView 编程实训平台 API")
                        .version("1.0.0")
                        .description("基于Spring Boot 3 + Vue 3的编程实训平台API文档")
                        .contact(new Contact()
                                        .name("AnyView Team")
                                        .email("support@anyview.com")))
                .addServersItem(new Server().url("http://localhost:8080").description("开发环境"))
                .addServersItem(new Server().url("https://api.anyview.com").description("生产环境"));
    }
}
