package com.example.skygarden.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.skygarden.constants.Constants;

/**
 * ScreenNameConverterのテストクラス
 */
class ScreenNameConverterTest {

    @Test
    void testConvertModeToScreenName_Content() {
        String result = ScreenNameConverter.convertModeToScreenName("");
        assertEquals(Constants.SCREEN_NAME_CONTENT, result);
    }

    @Test
    void testConvertModeToScreenName_Null() {
        String result = ScreenNameConverter.convertModeToScreenName(null);
        assertEquals(Constants.SCREEN_NAME_CONTENT, result);
    }

    @Test
    void testConvertModeToScreenName_Template() {
        String result = ScreenNameConverter.convertModeToScreenName(Constants.CONTENT_TYPE_TEMPLATE);
        assertEquals(Constants.SCREEN_NAME_TEMPLATE, result);
    }

    @Test
    void testConvertModeToScreenName_Element() {
        String result = ScreenNameConverter.convertModeToScreenName(Constants.CONTENT_TYPE_ELEMENT);
        assertEquals(Constants.SCREEN_NAME_ELEMENT, result);
    }

    @Test
    void testConvertModeToScreenName_Stylesheet() {
        String result = ScreenNameConverter.convertModeToScreenName(Constants.CONTENT_TYPE_STYLESHEET);
        assertEquals(Constants.SCREEN_NAME_STYLESHEET, result);
    }

    @Test
    void testConvertModeToScreenName_Script() {
        String result = ScreenNameConverter.convertModeToScreenName(Constants.CONTENT_TYPE_SCRIPT);
        assertEquals(Constants.SCREEN_NAME_SCRIPT, result);
    }

    @Test
    void testConvertModeToScreenName_Image() {
        String result = ScreenNameConverter.convertModeToScreenName(Constants.CONTENT_TYPE_IMAGE);
        assertEquals(Constants.SCREEN_NAME_IMAGE, result);
    }

    @Test
    void testConvertModeToScreenName_File() {
        String result = ScreenNameConverter.convertModeToScreenName(Constants.CONTENT_TYPE_FILE);
        assertEquals(Constants.SCREEN_NAME_FILE, result);
    }

    @Test
    void testConvertModeToScreenName_Movie() {
        String result = ScreenNameConverter.convertModeToScreenName(Constants.CONTENT_TYPE_MOVIE);
        assertEquals(Constants.SCREEN_NAME_MOVIE, result);
    }

    @Test
    void testConvertModeToScreenName_Unknown() {
        String result = ScreenNameConverter.convertModeToScreenName("unknown");
        assertEquals(Constants.SCREEN_NAME_CONTENT, result);
    }

    @Test
    void testGetPageUrlByMode_Content() {
        String result = ScreenNameConverter.getPageUrlByMode("");
        assertEquals(Constants.PATH_ROOT + "?mode=", result);
    }

    @Test
    void testGetPageUrlByMode_Null() {
        String result = ScreenNameConverter.getPageUrlByMode(null);
        assertEquals(Constants.PATH_ROOT + "?mode=", result);
    }

    @Test
    void testGetPageUrlByMode_Template() {
        String result = ScreenNameConverter.getPageUrlByMode(Constants.CONTENT_TYPE_TEMPLATE);
        assertEquals(Constants.PATH_ROOT + "?mode=template", result);
    }

    @Test
    void testGetPageUrlByMode_Element() {
        String result = ScreenNameConverter.getPageUrlByMode(Constants.CONTENT_TYPE_ELEMENT);
        assertEquals(Constants.PATH_ROOT + "?mode=element", result);
    }

    @Test
    void testGetPageUrlByMode_Image() {
        String result = ScreenNameConverter.getPageUrlByMode(Constants.CONTENT_TYPE_IMAGE);
        assertEquals(Constants.PATH_ROOT + "?mode=image", result);
    }

    @Test
    void testGetPageUrlByMode_File() {
        String result = ScreenNameConverter.getPageUrlByMode(Constants.CONTENT_TYPE_FILE);
        assertEquals(Constants.PATH_ROOT + "?mode=file", result);
    }

    @Test
    void testGetPageUrlByMode_Movie() {
        String result = ScreenNameConverter.getPageUrlByMode(Constants.CONTENT_TYPE_MOVIE);
        assertEquals(Constants.PATH_ROOT + "?mode=movie", result);
    }

    @Test
    void testGetRedirectUrlByType_Content() {
        String result = ScreenNameConverter.getRedirectUrlByType("");
        assertEquals(Constants.PATH_ROOT, result);
    }

    @Test
    void testGetRedirectUrlByType_Null() {
        String result = ScreenNameConverter.getRedirectUrlByType(null);
        assertEquals(Constants.PATH_ROOT, result);
    }

    @Test
    void testGetRedirectUrlByType_Template() {
        String result = ScreenNameConverter.getRedirectUrlByType(Constants.CONTENT_TYPE_TEMPLATE);
        assertEquals(Constants.PATH_ROOT + "?mode=template", result);
    }

    @Test
    void testGetRedirectUrlByType_Element() {
        String result = ScreenNameConverter.getRedirectUrlByType(Constants.CONTENT_TYPE_ELEMENT);
        assertEquals(Constants.PATH_ROOT + "?mode=element", result);
    }

    @Test
    void testGetRedirectUrlByType_Image() {
        String result = ScreenNameConverter.getRedirectUrlByType(Constants.CONTENT_TYPE_IMAGE);
        assertEquals(Constants.PATH_ROOT + "?mode=image", result);
    }

    @Test
    void testGetRedirectUrlByType_File() {
        String result = ScreenNameConverter.getRedirectUrlByType(Constants.CONTENT_TYPE_FILE);
        assertEquals(Constants.PATH_ROOT + "?mode=file", result);
    }

    @Test
    void testGetRedirectUrlByType_Movie() {
        String result = ScreenNameConverter.getRedirectUrlByType(Constants.CONTENT_TYPE_MOVIE);
        assertEquals(Constants.PATH_ROOT + "?mode=movie", result);
    }

    @Test
    void testGetRedirectUrlByType_Unknown() {
        String result = ScreenNameConverter.getRedirectUrlByType("unknown");
        assertEquals(Constants.PATH_ROOT, result);
    }
}
