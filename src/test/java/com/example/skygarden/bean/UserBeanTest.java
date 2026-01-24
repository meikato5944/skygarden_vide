package com.example.skygarden.bean;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * UserBeanのテストクラス
 */
class UserBeanTest {

    private UserBean bean;

    @BeforeEach
    void setUp() {
        bean = new UserBean();
    }

    @Test
    void testDefaultValues() {
        assertEquals("", bean.getName());
        assertEquals("", bean.getPassword());
        assertEquals("", bean.getEmail());
        assertEquals("", bean.getAdmin());
    }

    @Test
    void testSetAndGetName() {
        bean.setName("testuser");
        assertEquals("testuser", bean.getName());
    }

    @Test
    void testSetAndGetPassword() {
        bean.setPassword("password123");
        assertEquals("password123", bean.getPassword());
    }

    @Test
    void testSetAndGetEmail() {
        bean.setEmail("test@example.com");
        assertEquals("test@example.com", bean.getEmail());
    }

    @Test
    void testSetAndGetAdmin() {
        bean.setAdmin("1");
        assertEquals("1", bean.getAdmin());
        
        bean.setAdmin("0");
        assertEquals("0", bean.getAdmin());
    }

    @Test
    void testNullValues() {
        bean.setName(null);
        assertNull(bean.getName());
        
        bean.setPassword(null);
        assertNull(bean.getPassword());
        
        bean.setEmail(null);
        assertNull(bean.getEmail());
        
        bean.setAdmin(null);
        assertNull(bean.getAdmin());
    }

    @Test
    void testEmptyStringValues() {
        bean.setName("");
        assertEquals("", bean.getName());
        
        bean.setPassword("");
        assertEquals("", bean.getPassword());
    }

    @Test
    void testLongStringValues() {
        String longString = "a".repeat(1000);
        bean.setName(longString);
        assertEquals(longString, bean.getName());
    }

    @Test
    void testSpecialCharacters() {
        bean.setName("user&name<test>");
        assertEquals("user&name<test>", bean.getName());
        
        bean.setEmail("test+tag@example.com");
        assertEquals("test+tag@example.com", bean.getEmail());
    }

    @Test
    void testEmailFormats() {
        bean.setEmail("test@example.com");
        assertEquals("test@example.com", bean.getEmail());
        
        bean.setEmail("test.user@example.co.jp");
        assertEquals("test.user@example.co.jp", bean.getEmail());
        
        bean.setEmail("test+tag@example.com");
        assertEquals("test+tag@example.com", bean.getEmail());
    }

    @Test
    void testAdminFlags() {
        bean.setAdmin("1");
        assertEquals("1", bean.getAdmin());
        
        bean.setAdmin("0");
        assertEquals("0", bean.getAdmin());
        
        bean.setAdmin("");
        assertEquals("", bean.getAdmin());
    }

    @Test
    void testPasswordWithSpecialCharacters() {
        bean.setPassword("P@ssw0rd!123");
        assertEquals("P@ssw0rd!123", bean.getPassword());
    }

    @Test
    void testUnicodeCharacters() {
        bean.setName("ユーザー名");
        assertEquals("ユーザー名", bean.getName());
        
        bean.setEmail("テスト@example.com");
        assertEquals("テスト@example.com", bean.getEmail());
    }
}
