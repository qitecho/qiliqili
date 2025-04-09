package com.example.qiliqili.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API 文档")
                        .version("1.0")
                        .description("Spring Boot + Knife4j 示例"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addParameters("Authorization", new Parameter()
                                .name("Authorization")
                                .in("header")
                                .description("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMDI5ZGQwY2UyNTM0NzU0OTgxMDBkZmE1OWNiNTBhNCIsInN1YiI6IjIiLCJyb2xlIjoidXNlciIsImlzcyI6Imh0dHBzOi8vYXBpLnRlcml0ZXJpLmZ1biIsImlhdCI6MTc0MzA4MDM0MCwiZXhwIjoxNzQzMjUzMTQwfQ.pdCQFBOFEGrSSEASGNV2qcmRcg1f0An-h4H7Q2DpzjM")
                                .required(false) // 可选
                                .schema(new io.swagger.v3.oas.models.media.StringSchema())));
    }
}