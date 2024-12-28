package com.ktds.krater.management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정을 위한 클래스입니다.
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("사용량 관리 API")
                        .description("사용량 업데이트 이벤트를 발행하는 API")
                        .version("1.0.0"));
    }
}
