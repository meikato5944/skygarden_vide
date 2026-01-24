package com.example.skygarden.constants;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Constantsのテストクラス
 */
class ConstantsTest {

    @Test
    void testConstants_ContentTypes() {
        assertEquals("", Constants.CONTENT_TYPE_CONTENT);
        assertEquals("template", Constants.CONTENT_TYPE_TEMPLATE);
        assertEquals("element", Constants.CONTENT_TYPE_ELEMENT);
        assertEquals("stylesheet", Constants.CONTENT_TYPE_STYLESHEET);
        assertEquals("script", Constants.CONTENT_TYPE_SCRIPT);
        assertEquals("image", Constants.CONTENT_TYPE_IMAGE);
        assertEquals("file", Constants.CONTENT_TYPE_FILE);
        assertEquals("movie", Constants.CONTENT_TYPE_MOVIE);
    }

    @Test
    void testConstants_Flags() {
        assertEquals("1", Constants.FLAG_YES);
        assertEquals("0", Constants.FLAG_NO);
    }

    @Test
    void testConstants_Paths() {
        assertEquals("/", Constants.PATH_ROOT);
        assertEquals("/login", Constants.PATH_LOGIN);
        assertEquals("/content", Constants.PATH_CONTENT);
        assertEquals("/user-list", Constants.PATH_USER_LIST);
        assertEquals("/user", Constants.PATH_USER);
        assertEquals("/setting", Constants.PATH_SETTING);
        assertEquals("/webadmin", Constants.PATH_WEBADMIN);
    }

    @Test
    void testConstants_Tables() {
        assertEquals("content", Constants.TABLE_CONTENT);
        assertEquals("content_public", Constants.TABLE_CONTENT_PUBLIC);
        assertEquals("user", Constants.TABLE_USER);
        assertEquals("config", Constants.TABLE_CONFIG);
    }

    @Test
    void testConstants_Placeholders() {
        assertEquals("###content###", Constants.TEMPLATE_CONTENT_PLACEHOLDER);
        assertEquals("###title###", Constants.TEMPLATE_TITLE_PLACEHOLDER);
        assertEquals("###head###", Constants.TEMPLATE_HEAD_PLACEHOLDER);
        assertEquals("###", Constants.ELEMENT_PREFIX);
        assertEquals("element(", Constants.ELEMENT_FUNCTION_START);
        assertEquals(")", Constants.ELEMENT_FUNCTION_END);
    }

    @Test
    void testConstants_DateFormat() {
        assertEquals("yyyy-MM-dd HH:mm", Constants.DATE_FORMAT_DATETIME);
    }

    @Test
    void testConstants_EmptyString() {
        assertEquals("", Constants.EMPTY_STRING);
    }

    @Test
    void testConstants_SortOptions() {
        assertNotNull(Constants.SORT_OPTIONS_CONTENT);
        assertTrue(Constants.SORT_OPTIONS_CONTENT.length > 0);
        assertNotNull(Constants.SORT_OPTIONS_USER);
        assertTrue(Constants.SORT_OPTIONS_USER.length > 0);
    }
}
