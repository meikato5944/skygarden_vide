package com.example.skygarden.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ElementItemBeanのテストクラス
 */
class ElementItemBeanTest {

    private ElementItemBean bean;

    @BeforeEach
    void setUp() {
        bean = new ElementItemBean();
    }

    @Test
    void testDefaultValues() {
        assertNotNull(bean.getResults());
        assertTrue(bean.getResults().isEmpty());
    }

    @Test
    void testSetAndGetResults() {
        List<HashMap<String, String>> results = new ArrayList<>();
        HashMap<String, String> element1 = new HashMap<>();
        element1.put("id", "1");
        element1.put("title", "Header");
        element1.put("code", "#000000");
        results.add(element1);
        
        HashMap<String, String> element2 = new HashMap<>();
        element2.put("id", "2");
        element2.put("title", "Footer");
        element2.put("code", "#FFFFFF");
        results.add(element2);
        
        bean.setResults(results);
        assertEquals(2, bean.getResults().size());
        assertEquals("1", bean.getResults().get(0).get("id"));
        assertEquals("Header", bean.getResults().get(0).get("title"));
        assertEquals("#000000", bean.getResults().get(0).get("code"));
        assertEquals("2", bean.getResults().get(1).get("id"));
        assertEquals("Footer", bean.getResults().get(1).get("title"));
        assertEquals("#FFFFFF", bean.getResults().get(1).get("code"));
    }

    @Test
    void testResults_AddElement() {
        HashMap<String, String> element = new HashMap<>();
        element.put("id", "3");
        element.put("title", "Sidebar");
        element.put("elementcolor", "#FF0000");
        bean.getResults().add(element);
        
        assertEquals(1, bean.getResults().size());
        assertEquals("3", bean.getResults().get(0).get("id"));
        assertEquals("Sidebar", bean.getResults().get(0).get("title"));
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
    void testResults_MultipleElements() {
        for (int i = 1; i <= 10; i++) {
            HashMap<String, String> element = new HashMap<>();
            element.put("id", String.valueOf(i));
            element.put("title", "Element " + i);
            bean.getResults().add(element);
        }
        
        assertEquals(10, bean.getResults().size());
    }

    @Test
    void testResults_ElementWithAllFields() {
        HashMap<String, String> element = new HashMap<>();
        element.put("id", "1");
        element.put("title", "Test Element");
        element.put("code", "#FF0000");
        element.put("elementcolor", "#FF0000");
        element.put("content", "<div>Test</div>");
        bean.getResults().add(element);
        
        assertEquals(1, bean.getResults().size());
        HashMap<String, String> result = bean.getResults().get(0);
        assertEquals("1", result.get("id"));
        assertEquals("Test Element", result.get("title"));
        assertEquals("#FF0000", result.get("code"));
        assertEquals("#FF0000", result.get("elementcolor"));
        assertEquals("<div>Test</div>", result.get("content"));
    }

    @Test
    void testResults_ElementWithNullValues() {
        HashMap<String, String> element = new HashMap<>();
        element.put("id", null);
        element.put("title", null);
        bean.getResults().add(element);
        
        assertEquals(1, bean.getResults().size());
        assertNull(bean.getResults().get(0).get("id"));
        assertNull(bean.getResults().get(0).get("title"));
    }

    @Test
    void testResults_ElementWithEmptyValues() {
        HashMap<String, String> element = new HashMap<>();
        element.put("id", "");
        element.put("title", "");
        bean.getResults().add(element);
        
        assertEquals(1, bean.getResults().size());
        assertEquals("", bean.getResults().get(0).get("id"));
        assertEquals("", bean.getResults().get(0).get("title"));
    }
}
