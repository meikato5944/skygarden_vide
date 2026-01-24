package com.example.skygarden.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ListBeanのテストクラス
 */
class ListBeanTest {

    private ListBean bean;

    @BeforeEach
    void setUp() {
        bean = new ListBean();
    }

    @Test
    void testDefaultValues() {
        assertEquals("", bean.getLoginName());
        assertEquals("", bean.getScreenName());
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
    void testSetAndGetScreenName() {
        bean.setScreenName("コンテンツ");
        assertEquals("コンテンツ", bean.getScreenName());
    }

    @Test
    void testSetAndGetRegisterMessage() {
        bean.setRegisterMessage("登録しました。");
        assertEquals("登録しました。", bean.getRegisterMessage());
    }

    @Test
    void testSetAndGetSortOutput() {
        bean.setSortOutput("<option value=\"updated desc\">更新日時(降順)</option>");
        assertEquals("<option value=\"updated desc\">更新日時(降順)</option>", bean.getSortOutput());
    }

    @Test
    void testSetAndGetPagerOutput() {
        bean.setPagerOutput("<li class=\"page-item\">1</li>");
        assertEquals("<li class=\"page-item\">1</li>", bean.getPagerOutput());
    }

    @Test
    void testSetAndGetResults() {
        List<HashMap<String, String>> results = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("title", "Test Content 1");
        content1.put("url", "test1");
        results.add(content1);
        
        HashMap<String, String> content2 = new HashMap<>();
        content2.put("id", "2");
        content2.put("title", "Test Content 2");
        content2.put("url", "test2");
        results.add(content2);
        
        bean.setResults(results);
        assertEquals(2, bean.getResults().size());
        assertEquals("1", bean.getResults().get(0).get("id"));
        assertEquals("Test Content 1", bean.getResults().get(0).get("title"));
        assertEquals("test1", bean.getResults().get(0).get("url"));
    }

    @Test
    void testResults_AddContent() {
        HashMap<String, String> content = new HashMap<>();
        content.put("id", "1");
        content.put("title", "New Content");
        content.put("type", "");
        content.put("updated", "2024-01-01 12:00");
        bean.getResults().add(content);
        
        assertEquals(1, bean.getResults().size());
        assertEquals("1", bean.getResults().get(0).get("id"));
    }

    @Test
    void testResults_MultipleContents() {
        for (int i = 1; i <= 5; i++) {
            HashMap<String, String> content = new HashMap<>();
            content.put("id", String.valueOf(i));
            content.put("title", "Content " + i);
            bean.getResults().add(content);
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
        
        bean.setScreenName(null);
        assertNull(bean.getScreenName());
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
    void testResults_ContentWithAllFields() {
        HashMap<String, String> content = new HashMap<>();
        content.put("id", "1");
        content.put("title", "Test Title");
        content.put("url", "test/url");
        content.put("type", "");
        content.put("created", "2024-01-01 10:00");
        content.put("updated", "2024-01-01 12:00");
        content.put("created_by", "user1");
        content.put("updated_by", "user1");
        bean.getResults().add(content);
        
        assertEquals(1, bean.getResults().size());
        HashMap<String, String> result = bean.getResults().get(0);
        assertEquals("1", result.get("id"));
        assertEquals("Test Title", result.get("title"));
        assertEquals("test/url", result.get("url"));
        assertEquals("", result.get("type"));
    }
}
