package com.struggle.common.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class Swagger3Config {

    @Bean
    public OpenAPI springShopOpenAPI() {
        //信息
        Info info = new Info()
                .title("swagger3 struggle接口文档")
                .description("springboot-swagger3 接口文档：/v3/api-docs")
                .version("v1.0.0");

        //鉴权组件(随便起名的)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")//固定写法
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        Components components = new Components().addSecuritySchemes("Authorization", securityScheme);

        //鉴权限制要求(随便起名的)
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization", Arrays.asList("read", "write"));

        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
