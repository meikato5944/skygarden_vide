package com.example.skygarden.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.skygarden.logic.CommonProc;

/**
 * CORS（Cross-Origin Resource Sharing）設定クラス
 * フロントエンドからのリクエストを許可するための設定を提供する
 */
@Configuration
public class CorsConfig {
    /**
     * CORS設定を構成する
     * フロントエンドからのリクエストを許可し、クッキー（セッション）を含める設定を行う
     * 
     * @return WebMvcConfigurerの実装
     */
    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * CORSマッピングを追加する
             * /webadmin/** パスに対してフロントエンドからのリクエストを許可する
             * 
             * @param registry CORSレジストリ
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/webadmin/**")
                        .allowedOrigins(CommonProc.FRONTEND_PATH) // Fontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true); // クッキー（セッション）を含める
            }
        };
    }
}