package com.example.skygarden.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Settingのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class SettingTest {

    @Mock
    private ContentMapper mapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private Setting setting;

    @BeforeEach
    void setUp() {
        // デフォルトのモック設定
    }

    @Test
    void testDoUpdate_AllParameters() throws IOException {
        when(request.getParameter(Constants.CONFIG_ELEMENTS_COLOR_VALUE)).thenReturn("header=#000000*footer=#333333*");
        when(request.getParameter(Constants.CONFIG_DEFAULT_PUBLISH_ON)).thenReturn("1");
        when(request.getParameter(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(request.getParameter(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-4");
        when(request.getParameter(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title");
        when(request.getParameter(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Generate content");
        when(request.getParameter(Constants.CONFIG_AI_GENERATION_VISIBLE)).thenReturn("1");
        when(request.getParameter(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(request.getParameter(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(request.getParameter(Constants.CONFIG_EMAIL_FROM)).thenReturn("noreply@example.com");
        when(request.getParameter(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(request.getParameter(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        setting.doUpdate(request, response, session);

        verify(mapper).updateSetting("header=#000000*footer=#333333*", Constants.CONFIG_ELEMENTS_COLOR_VALUE);
        verify(mapper).updateSetting("1", Constants.CONFIG_DEFAULT_PUBLISH_ON);
        verify(mapper).updateSetting("sk-test-key", Constants.CONFIG_OPENAI_API_KEY);
        verify(mapper).updateSetting("gpt-4", Constants.CONFIG_OPENAI_MODEL);
        verify(mapper).updateSetting("Generate title", Constants.CONFIG_OPENAI_PROMPT_TITLE);
        verify(mapper).updateSetting("Generate content", Constants.CONFIG_OPENAI_PROMPT_CONTENT);
        verify(mapper).updateSetting("1", Constants.CONFIG_AI_GENERATION_VISIBLE);
        verify(mapper).updateSetting("1", Constants.CONFIG_EMAIL_ENABLED);
        verify(mapper).updateSetting("test@example.com", Constants.CONFIG_EMAIL_TO);
        verify(mapper).updateSetting("noreply@example.com", Constants.CONFIG_EMAIL_FROM);
        verify(mapper).updateSetting("Template", Constants.CONFIG_EMAIL_BODY_TEMPLATE);
        verify(mapper).updateSetting("http://example.com", Constants.CONFIG_EMAIL_BASE_URL);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_SETTING_REGISTER_SUCCESS);
        verify(response).sendRedirect(Constants.PATH_ROOT);
    }

    @Test
    void testDoUpdate_NullParameters() throws IOException {
        when(request.getParameter(Constants.CONFIG_ELEMENTS_COLOR_VALUE)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_DEFAULT_PUBLISH_ON)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_OPENAI_API_KEY)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_OPENAI_MODEL)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_AI_GENERATION_VISIBLE)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_EMAIL_ENABLED)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_EMAIL_TO)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_EMAIL_FROM)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn(null);
        when(request.getParameter(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn(null);

        setting.doUpdate(request, response, session);

        verify(mapper).updateSetting(null, Constants.CONFIG_ELEMENTS_COLOR_VALUE);
        verify(mapper).updateSetting(Constants.FLAG_NO, Constants.CONFIG_DEFAULT_PUBLISH_ON);
        verify(mapper).updateSetting(Constants.EMPTY_STRING, Constants.CONFIG_OPENAI_API_KEY);
        verify(mapper).updateSetting("gpt-3.5-turbo", Constants.CONFIG_OPENAI_MODEL);
        verify(mapper).updateSetting(Constants.FLAG_NO, Constants.CONFIG_AI_GENERATION_VISIBLE);
        verify(mapper).updateSetting(Constants.FLAG_NO, Constants.CONFIG_EMAIL_ENABLED);
    }

    @Test
    void testDoUpdate_EmptyOpenAIModel() throws IOException {
        when(request.getParameter(Constants.CONFIG_ELEMENTS_COLOR_VALUE)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_DEFAULT_PUBLISH_ON)).thenReturn("0");
        when(request.getParameter(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_OPENAI_MODEL)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_AI_GENERATION_VISIBLE)).thenReturn("0");
        when(request.getParameter(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("0");
        when(request.getParameter(Constants.CONFIG_EMAIL_TO)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_EMAIL_FROM)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("");
        when(request.getParameter(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("");

        setting.doUpdate(request, response, session);

        verify(mapper).updateSetting("gpt-3.5-turbo", Constants.CONFIG_OPENAI_MODEL);
    }

    @Test
    void testDoUpdate_IOException() throws IOException {
        when(request.getParameter(anyString())).thenReturn("");
        doThrow(new IOException("Redirect error")).when(response).sendRedirect(anyString());

        setting.doUpdate(request, response, session);

        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_SETTING_REGISTER_SUCCESS);
    }

    @Test
    void testElementsColorList_ValidData() {
        String colorData = "header=#000000*footer=#333333*sidebar=#FF0000*";
        when(mapper.getElementColor()).thenReturn(colorData);

        List<HashMap<String, String>> result = setting.elementsColorList();

        assertEquals(3, result.size());
        assertEquals("header", result.get(0).get("name"));
        assertEquals("#000000", result.get(0).get("code"));
        assertEquals("footer", result.get(1).get("name"));
        assertEquals("#333333", result.get(1).get("code"));
        assertEquals("sidebar", result.get(2).get("name"));
        assertEquals("#FF0000", result.get(2).get("code"));
    }

    @Test
    void testElementsColorList_EmptyString() {
        when(mapper.getElementColor()).thenReturn("");

        List<HashMap<String, String>> result = setting.elementsColorList();

        assertTrue(result.isEmpty());
    }

    @Test
    void testElementsColorList_Null() {
        when(mapper.getElementColor()).thenReturn(null);

        List<HashMap<String, String>> result = setting.elementsColorList();

        assertTrue(result.isEmpty());
    }

    @Test
    void testElementsColorList_SingleElement() {
        when(mapper.getElementColor()).thenReturn("header=#000000*");

        List<HashMap<String, String>> result = setting.elementsColorList();

        assertEquals(1, result.size());
        assertEquals("header", result.get(0).get("name"));
        assertEquals("#000000", result.get(0).get("code"));
    }

    @Test
    void testElementsColorList_InvalidFormat() {
        when(mapper.getElementColor()).thenReturn("invalid*format*");

        List<HashMap<String, String>> result = setting.elementsColorList();

        // 無効な形式の要素はスキップされる
        assertTrue(result.isEmpty());
    }

    @Test
    void testElementsColorList_EmptyElements() {
        when(mapper.getElementColor()).thenReturn("header=#000000**footer=#333333*");

        List<HashMap<String, String>> result = setting.elementsColorList();

        assertEquals(2, result.size());
    }

    @Test
    void testElementsColorList_NoEqualsSign() {
        when(mapper.getElementColor()).thenReturn("header*footer*");

        List<HashMap<String, String>> result = setting.elementsColorList();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDefaultPublishOn_Yes() {
        when(mapper.getSettingByName(Constants.CONFIG_DEFAULT_PUBLISH_ON)).thenReturn("1");

        String result = setting.getDefaultPublishOn();

        assertEquals("1", result);
    }

    @Test
    void testGetDefaultPublishOn_No() {
        when(mapper.getSettingByName(Constants.CONFIG_DEFAULT_PUBLISH_ON)).thenReturn("0");

        String result = setting.getDefaultPublishOn();

        assertEquals("0", result);
    }

    @Test
    void testGetDefaultPublishOn_Null() {
        when(mapper.getSettingByName(Constants.CONFIG_DEFAULT_PUBLISH_ON)).thenReturn(null);

        String result = setting.getDefaultPublishOn();

        assertEquals(Constants.FLAG_NO, result);
    }

    @Test
    void testGetOpenAISetting_Exists() {
        when(mapper.getSettingByName(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");

        String result = setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY);

        assertEquals("sk-test-key", result);
    }

    @Test
    void testGetOpenAISetting_NotExists() {
        when(mapper.getSettingByName(Constants.CONFIG_OPENAI_API_KEY)).thenReturn(null);

        String result = setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY);

        assertEquals(Constants.EMPTY_STRING, result);
    }

    @Test
    void testGetAIGenerationVisible_Yes() {
        when(mapper.getSettingByName(Constants.CONFIG_AI_GENERATION_VISIBLE)).thenReturn("1");

        String result = setting.getAIGenerationVisible();

        assertEquals("1", result);
    }

    @Test
    void testGetAIGenerationVisible_No() {
        when(mapper.getSettingByName(Constants.CONFIG_AI_GENERATION_VISIBLE)).thenReturn("0");

        String result = setting.getAIGenerationVisible();

        assertEquals("0", result);
    }

    @Test
    void testGetAIGenerationVisible_Null() {
        when(mapper.getSettingByName(Constants.CONFIG_AI_GENERATION_VISIBLE)).thenReturn(null);

        String result = setting.getAIGenerationVisible();

        assertEquals(Constants.FLAG_NO, result);
    }

    @Test
    void testGetEmailSetting_Exists() {
        when(mapper.getSettingByName(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");

        String result = setting.getEmailSetting(Constants.CONFIG_EMAIL_TO);

        assertEquals("test@example.com", result);
    }

    @Test
    void testGetEmailSetting_NotExists() {
        when(mapper.getSettingByName(Constants.CONFIG_EMAIL_TO)).thenReturn(null);

        String result = setting.getEmailSetting(Constants.CONFIG_EMAIL_TO);

        assertEquals(Constants.EMPTY_STRING, result);
    }

    @Test
    void testGetDefaultEmailBody() {
        String result = setting.getDefaultEmailBody();

        assertNotNull(result);
        assertTrue(result.contains("###title###"));
        assertTrue(result.contains("###url###"));
        assertTrue(result.contains("###publish_date###"));
    }
}
