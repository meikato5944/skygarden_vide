package com.example.skygarden;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Applicationクラスのテスト
 * メインクラスの存在確認と基本的な動作確認を行います
 */
class ApplicationTest {

    @Test
    void testApplicationClassExists() {
        // Applicationクラスが存在することを確認
        assertNotNull(Application.class);
        assertTrue(Application.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        // mainメソッドが存在することを確認
        java.lang.reflect.Method mainMethod = Application.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }
}
