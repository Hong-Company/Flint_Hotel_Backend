package com.hotel.flint.common.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 허용 정책 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**") // ex. localhost:8081/---/--- 모두 허용
                .allowedOrigins("https://www.flinthotels.site") // 허용 url 명시 (나중에 우리 서버로 들어올 url여기에 추가)
//                .allowedOrigins("http://localhost:8081")
                .allowedMethods("*") // 어떤 메서드든 허용
                .allowedHeaders("*")
                .allowCredentials(true); // 보안처리할거냐 말거냐
    }
}
