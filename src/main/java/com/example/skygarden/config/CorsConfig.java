package com.example.skygarden.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.skygarden.constants.Constants;

/**
 * CORS（Cross-Origin Resource Sharing）設定クラス
 * 
 * このクラスはクロスオリジンリクエストの許可設定を行います。
 * 以前のSPA（Single Page Application）構成の名残で、
 * /webadmin/** パスへのCORSを設定しています。
 * 
 * 現在の構成（Thymeleaf SSR）では同一オリジンのため、
 * この設定は実質的に不要ですが、API単独使用時の互換性のために残されています。
 * 
 * 許可設定:
 * - 対象パス: /webadmin/**
 * - 許可オリジン: http://localhost:8080
 * - 許可メソッド: GET, POST, PUT, DELETE
 * - クレデンシャル: 許可（セッションクッキーの送信を許可）
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
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping(Constants.PATH_WEBADMIN + "/**")
                        .allowedOrigins("http://localhost:8080") // 同一オリジンなのでlocalhost:8080
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true); // クッキー（セッション）を含める
            }
        };
    }
}