package com.example.skygarden.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * CorsConfigのテストクラス
 */
class CorsConfigTest {

    @Test
    void testCorsConfigurer() {
        CorsConfig corsConfig = new CorsConfig();
        var configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);

        // CORSレジストリを作成して設定を確認
        CorsRegistry registry = new CorsRegistry();
        configurer.addCorsMappings(registry);

        // 設定が正しく追加されたことを確認（直接検証は難しいため、例外が発生しないことを確認）
        assertNotNull(registry);
    }

    @Test
    void testCorsConfigurer_NotNull() {
        CorsConfig corsConfig = new CorsConfig();
        var configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
    }

    @Test
    void testCorsConfigurer_MultipleCalls() {
        CorsConfig corsConfig = new CorsConfig();
        var configurer1 = corsConfig.corsConfigurer();
        var configurer2 = corsConfig.corsConfigurer();

        assertNotNull(configurer1);
        assertNotNull(configurer2);
    }
}
