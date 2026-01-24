package com.example.skygarden.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ContentBeanのテストクラス
 */
class ContentBeanTest {

    private ContentBean bean;

    @BeforeEach
    void setUp() {
        bean = new ContentBean();
    }

    @Test
    void testDefaultValues() {
        assertEquals("", bean.getScreenName());
        assertEquals("", bean.getSchedule_published());
        assertEquals("", bean.getSchedule_unpublished());
        assertEquals("", bean.getTemplate());
        assertEquals("", bean.getTitle());
        assertEquals("", bean.getHead());
        assertEquals("", bean.getContent());
        assertEquals("", bean.getUrl());
        assertEquals("", bean.getElementcolor());
        assertEquals("", bean.getTemplateOutput());
        assertEquals("", bean.getColorOutput());
        assertEquals("", bean.getPublishflgKeep());
        assertNotNull(bean.getEleResults());
        assertTrue(bean.getEleResults().isEmpty());
    }

    @Test
    void testSetAndGetScreenName() {
        bean.setScreenName("コンテンツ");
        assertEquals("コンテンツ", bean.getScreenName());
    }

    @Test
    void testSetAndGetSchedulePublished() {
        bean.setSchedule_published("2024-01-01 12:00");
        assertEquals("2024-01-01 12:00", bean.getSchedule_published());
    }

    @Test
    void testSetAndGetScheduleUnpublished() {
        bean.setSchedule_unpublished("2024-12-31 23:59");
        assertEquals("2024-12-31 23:59", bean.getSchedule_unpublished());
    }

    @Test
    void testSetAndGetTemplate() {
        bean.setTemplate("1");
        assertEquals("1", bean.getTemplate());
    }

    @Test
    void testSetAndGetTitle() {
        bean.setTitle("テストタイトル");
        assertEquals("テストタイトル", bean.getTitle());
    }

    @Test
    void testSetAndGetHead() {
        bean.setHead("<style>body { color: red; }</style>");
        assertEquals("<style>body { color: red; }</style>", bean.getHead());
    }

    @Test
    void testSetAndGetContent() {
        bean.setContent("<p>テストコンテンツ</p>");
        assertEquals("<p>テストコンテンツ</p>", bean.getContent());
    }

    @Test
    void testSetAndGetUrl() {
        bean.setUrl("test/page");
        assertEquals("test/page", bean.getUrl());
    }

    @Test
    void testSetAndGetElementcolor() {
        bean.setElementcolor("#FF0000");
        assertEquals("#FF0000", bean.getElementcolor());
    }

    @Test
    void testSetAndGetTemplateOutput() {
        bean.setTemplateOutput("<option value=\"1\">Template 1</option>");
        assertEquals("<option value=\"1\">Template 1</option>", bean.getTemplateOutput());
    }

    @Test
    void testSetAndGetColorOutput() {
        bean.setColorOutput("<option value=\"#FF0000\">Red</option>");
        assertEquals("<option value=\"#FF0000\">Red</option>", bean.getColorOutput());
    }

    @Test
    void testSetAndGetPublishflgKeep() {
        bean.setPublishflgKeep("1");
        assertEquals("1", bean.getPublishflgKeep());
    }

    @Test
    void testSetAndGetEleResults() {
        List<HashMap<String, String>> eleResults = new ArrayList<>();
        HashMap<String, String> element = new HashMap<>();
        element.put("id", "1");
        element.put("title", "Header");
        element.put("code", "#000000");
        eleResults.add(element);
        
        bean.setEleResults(eleResults);
        assertEquals(1, bean.getEleResults().size());
        assertEquals("1", bean.getEleResults().get(0).get("id"));
        assertEquals("Header", bean.getEleResults().get(0).get("title"));
        assertEquals("#000000", bean.getEleResults().get(0).get("code"));
    }

    @Test
    void testEleResults_AddElement() {
        HashMap<String, String> element = new HashMap<>();
        element.put("id", "2");
        element.put("title", "Footer");
        bean.getEleResults().add(element);
        
        assertEquals(1, bean.getEleResults().size());
        assertEquals("2", bean.getEleResults().get(0).get("id"));
    }

    @Test
    void testEleResults_MultipleElements() {
        HashMap<String, String> element1 = new HashMap<>();
        element1.put("id", "1");
        element1.put("title", "Header");
        bean.getEleResults().add(element1);
        
        HashMap<String, String> element2 = new HashMap<>();
        element2.put("id", "2");
        element2.put("title", "Footer");
        bean.getEleResults().add(element2);
        
        assertEquals(2, bean.getEleResults().size());
    }

    @Test
    void testNullValues() {
        bean.setScreenName(null);
        assertNull(bean.getScreenName());
        
        bean.setTitle(null);
        assertNull(bean.getTitle());
    }

    @Test
    void testEmptyStringValues() {
        bean.setScreenName("");
        assertEquals("", bean.getScreenName());
        
        bean.setTitle("");
        assertEquals("", bean.getTitle());
    }

    @Test
    void testLongStringValues() {
        String longString = "a".repeat(10000);
        bean.setContent(longString);
        assertEquals(longString, bean.getContent());
    }

    @Test
    void testSpecialCharacters() {
        bean.setTitle("テスト & <test> \"quote\" 'apostrophe'");
        assertEquals("テスト & <test> \"quote\" 'apostrophe'", bean.getTitle());
    }
}
