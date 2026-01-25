package com.example.skygarden;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

/**
 * Applicationクラスのテスト
 * メインクラスの存在確認と基本的な動作確認を行います
 */
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
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

    @Test
    void testMainMethodCanBeInvoked() {
        // mainメソッドが呼び出し可能であることを確認（実際には実行しない）
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method mainMethod = Application.class.getMethod("main", String[].class);
            assertNotNull(mainMethod);
        });
    }

    @Test
    void testApplicationHasMapperScanAnnotation() {
        // @MapperScanアノテーションが存在することを確認
        assertTrue(Application.class.isAnnotationPresent(org.mybatis.spring.annotation.MapperScan.class));
    }
}
