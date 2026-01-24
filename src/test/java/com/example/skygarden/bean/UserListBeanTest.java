package com.example.skygarden.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * UserListBeanのテストクラス
 */
class UserListBeanTest {

    private UserListBean bean;

    @BeforeEach
    void setUp() {
        bean = new UserListBean();
    }

    @Test
    void testDefaultValues() {
        assertEquals("", bean.getLoginName());
        assertEquals("", bean.getRegisterMessage());
        assertEquals("", bean.getSortOutput());
        assertEquals("", bean.getPagerOutput());
        assertNotNull(bean.getResults());
        assertTrue(bean.getResults().isEmpty());
    }

    @Test
    void testSetAndGetLoginName() {
        bean.setLoginName("testuser");
        assertEquals("testuser", bean.getLoginName());
    }

    @Test
    void testSetAndGetRegisterMessage() {
        bean.setRegisterMessage("登録しました。");
        assertEquals("登録しました。", bean.getRegisterMessage());
    }

    @Test
    void testSetAndGetSortOutput() {
        bean.setSortOutput("<option value=\"id desc\">ID(昇順)</option>");
        assertEquals("<option value=\"id desc\">ID(昇順)</option>", bean.getSortOutput());
    }

    @Test
    void testSetAndGetPagerOutput() {
        bean.setPagerOutput("<li class=\"page-item\">1</li>");
        assertEquals("<li class=\"page-item\">1</li>", bean.getPagerOutput());
    }

    @Test
    void testSetAndGetResults() {
        List<HashMap<String, String>> results = new ArrayList<>();
        HashMap<String, String> user1 = new HashMap<>();
        user1.put("id", "1");
        user1.put("name", "user1");
        user1.put("email", "user1@example.com");
        user1.put("admin", "1");
        results.add(user1);
        
        HashMap<String, String> user2 = new HashMap<>();
        user2.put("id", "2");
        user2.put("name", "user2");
        user2.put("email", "user2@example.com");
        user2.put("admin", "0");
        results.add(user2);
        
        bean.setResults(results);
        assertEquals(2, bean.getResults().size());
        assertEquals("1", bean.getResults().get(0).get("id"));
        assertEquals("user1", bean.getResults().get(0).get("name"));
        assertEquals("user1@example.com", bean.getResults().get(0).get("email"));
        assertEquals("1", bean.getResults().get(0).get("admin"));
    }

    @Test
    void testResults_AddUser() {
        HashMap<String, String> user = new HashMap<>();
        user.put("id", "1");
        user.put("name", "newuser");
        user.put("email", "newuser@example.com");
        user.put("admin", "0");
        bean.getResults().add(user);
        
        assertEquals(1, bean.getResults().size());
        assertEquals("1", bean.getResults().get(0).get("id"));
        assertEquals("newuser", bean.getResults().get(0).get("name"));
    }

    @Test
    void testResults_MultipleUsers() {
        for (int i = 1; i <= 5; i++) {
            HashMap<String, String> user = new HashMap<>();
            user.put("id", String.valueOf(i));
            user.put("name", "user" + i);
            user.put("email", "user" + i + "@example.com");
            user.put("admin", i % 2 == 0 ? "0" : "1");
            bean.getResults().add(user);
        }
        
        assertEquals(5, bean.getResults().size());
    }

    @Test
    void testResults_EmptyList() {
        bean.setResults(new ArrayList<>());
        assertTrue(bean.getResults().isEmpty());
    }

    @Test
    void testResults_NullList() {
        bean.setResults(null);
        assertNull(bean.getResults());
    }

    @Test
    void testNullValues() {
        bean.setLoginName(null);
        assertNull(bean.getLoginName());
        
        bean.setRegisterMessage(null);
        assertNull(bean.getRegisterMessage());
    }

    @Test
    void testEmptyStringValues() {
        bean.setLoginName("");
        assertEquals("", bean.getLoginName());
        
        bean.setRegisterMessage("");
        assertEquals("", bean.getRegisterMessage());
    }

    @Test
    void testLongStringValues() {
        String longString = "a".repeat(10000);
        bean.setPagerOutput(longString);
        assertEquals(longString, bean.getPagerOutput());
    }

    @Test
    void testSpecialCharacters() {
        bean.setRegisterMessage("登録しました。& <test> \"quote\"");
        assertEquals("登録しました。& <test> \"quote\"", bean.getRegisterMessage());
    }

    @Test
    void testResults_UserWithAllFields() {
        HashMap<String, String> user = new HashMap<>();
        user.put("id", "1");
        user.put("name", "Test User");
        user.put("password", "password123");
        user.put("email", "test@example.com");
        user.put("admin", "1");
        bean.getResults().add(user);
        
        assertEquals(1, bean.getResults().size());
        HashMap<String, String> result = bean.getResults().get(0);
        assertEquals("1", result.get("id"));
        assertEquals("Test User", result.get("name"));
        assertEquals("password123", result.get("password"));
        assertEquals("test@example.com", result.get("email"));
        assertEquals("1", result.get("admin"));
    }

    @Test
    void testResults_UserWithNullValues() {
        HashMap<String, String> user = new HashMap<>();
        user.put("id", "1");
        user.put("name", null);
        user.put("email", null);
        bean.getResults().add(user);
        
        assertEquals(1, bean.getResults().size());
        assertNull(bean.getResults().get(0).get("name"));
        assertNull(bean.getResults().get(0).get("email"));
    }
}
