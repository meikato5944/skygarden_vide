package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.skygarden.bean.ContentBean;
import com.example.skygarden.bean.ListBean;
import com.example.skygarden.config.AppProperties;
import com.example.skygarden.config.AppProperties.Pagination;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Content;
import com.example.skygarden.logic.Setting;
import com.example.skygarden.service.OpenAIService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ContentControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContentControllerTest {

    @Mock
    private Content content;

    @Mock
    private Setting setting;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private Pagination pagination;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ContentController controller;

    @BeforeEach
    void setUp() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
    }

    @Test
    void testUpdate_Create() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        when(content.doCreate(any(), any(), any())).thenReturn(true);

        controller.update("", request, response, session);

        verify(content).doCreate(request, response, session);
        verify(content, never()).doUpdate(any(), any(), any());
    }

    @Test
    void testUpdate_Update() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        when(content.doUpdate(any(), any(), any())).thenReturn(true);

        controller.update("1", request, response, session);

        verify(content).doUpdate(request, response, session);
        verify(content, never()).doCreate(any(), any(), any());
    }

    @Test
    void testGetById_WithId() throws IOException {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("id", "1");
        contentData.put("title", "Test Title");
        contentData.put("template", "");
        contentData.put("head", "");
        contentData.put("content", "Test Content");
        contentData.put("url", "test/page");
        contentData.put("elementcolor", "");
        contentData.put("schedule_published", "");
        contentData.put("schedule_unpublished", "");
        contentData.put("publishflg_keep", "1");

        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn("");
        when(content.doSearch("1")).thenReturn(contentData);
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("1", "", request, response);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
    }

    @Test
    void testGetById_EmptyId() throws IOException {
        when(request.getParameter("id")).thenReturn("");
        when(request.getParameter("mode")).thenReturn("");
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("", "", request, response);

        assertNotNull(result);
        assertEquals("", result.getTitle());
        verify(request, atLeastOnce()).getParameter("id");
        verify(request, atLeastOnce()).getParameter("mode");
    }

    @Test
    void testGetById_TemplateMode() throws IOException {
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("id", "1");
        templateData.put("title", "Template");
        templateData.put("content", "###element(10),###content###");
        templateData.put("head", "");

        HashMap<String, String> elementData = new HashMap<>();
        elementData.put("id", "10");
        elementData.put("title", "Element 10");
        elementData.put("elementcolor", "#FF0000");

        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn(Constants.CONTENT_TYPE_TEMPLATE);
        when(content.doSearch("1")).thenReturn(templateData);
        when(content.doSearch("10")).thenReturn(elementData);
        when(setting.elementsColorList()).thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("1", Constants.CONTENT_TYPE_TEMPLATE, request, response);

        assertNotNull(result);
        assertEquals("Template", result.getTitle());
    }

    @Test
    void testGetTemplate() throws IOException {
        List<HashMap<String, String>> templates = new ArrayList<>();
        HashMap<String, String> template1 = new HashMap<>();
        template1.put("id", "1");
        template1.put("title", "Template 1");
        templates.add(template1);

        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(templates);

        String result = controller.getTemplate(request, response);

        assertNotNull(result);
        assertTrue(result.contains("Template 1"));
        assertTrue(result.contains("<option"));
    }

    @Test
    void testGetTemplate_EmptyList() throws IOException {
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        String result = controller.getTemplate(request, response);

        assertNotNull(result);
        assertTrue(result.contains("--none--"));
    }

    @Test
    void testGetElement() throws IOException {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        HashMap<String, String> color1 = new HashMap<>();
        color1.put("name", "header");
        color1.put("code", "#000000");
        colorElements.add(color1);

        when(setting.elementsColorList()).thenReturn(colorElements);

        String result = controller.getElement(request, response);

        assertNotNull(result);
        assertTrue(result.contains("header"));
        assertTrue(result.contains("#000000"));
    }

    @Test
    void testGetElement_EmptyList() throws IOException {
        when(setting.elementsColorList()).thenReturn(new ArrayList<>());

        String result = controller.getElement(request, response);

        assertNotNull(result);
        assertTrue(result.contains("--none--"));
    }

    @Test
    void testDelete() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        when(content.doDelete(anyString(), anyString(), any(), any())).thenReturn(true);
        doNothing().when(response).sendRedirect(anyString());

        controller.delete("1", "", request, response);

        verify(content, times(1)).doDelete("1", "", response, session);
    }

    @Test
    void testDelete_EmptyId() throws IOException {
        when(request.getSession(true)).thenReturn(session);

        controller.delete("", "", request, response);

        verify(content, never()).doDelete(anyString(), anyString(), any(), any());
    }

    @Test
    void testGetListApi_WithKeyword() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn("testuser");
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn("登録しました。");

        List<HashMap<String, String>> results = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("title", "Test");
        results.add(content1);

        when(content.searchList("updated desc", 1, "", "test")).thenReturn(results);
        when(content.getSearchPager(1, "", "updated desc", "test")).thenReturn("<li>1</li>");

        ListBean result = controller.getListApi("", "updated desc", "1", "test", request, response, session);

        assertNotNull(result);
        assertEquals("testuser", result.getLoginName());
        assertEquals("登録しました。", result.getRegisterMessage());
        assertEquals(1, result.getResults().size());
        verify(session).setAttribute(Constants.SESSION_LOGIN_NAME, Constants.EMPTY_STRING);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
    }

    @Test
    void testGetListApi_WithoutKeyword() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);

        List<HashMap<String, String>> results = new ArrayList<>();
        when(content.getList("updated desc", 1, "")).thenReturn(results);
        when(content.getPager(1, "", "updated desc")).thenReturn("");

        ListBean result = controller.getListApi("", "updated desc", "1", "", request, response, session);

        assertNotNull(result);
        assertEquals("", result.getLoginName());
    }

    @Test
    void testUrlMatches_Matches() throws IOException {
        when(content.urlMatches("test/page", "1")).thenReturn(true);

        boolean result = controller.urlMatches("test/page", "1");

        assertTrue(result);
    }

    @Test
    void testUrlMatches_NoMatch() throws IOException {
        when(content.urlMatches("test/page", "1")).thenReturn(false);

        boolean result = controller.urlMatches("test/page", "1");

        assertFalse(result);
    }

    @Test
    void testConvertPreview() {
        when(content.previewContent("Test Content", null)).thenReturn("Processed Content");

        String result = controller.convertPreview("Test Content");

        assertEquals("Processed Content", result);
    }

    @Test
    void testConvertPreview_Empty() {
        when(content.previewContent("", null)).thenReturn("");

        String result = controller.convertPreview("");

        assertEquals("", result);
    }

    @Test
    void testGetImages() {
        List<HashMap<String, String>> images = new ArrayList<>();
        HashMap<String, String> image1 = new HashMap<>();
        image1.put("id", "1");
        image1.put("title", "Image 1");
        images.add(image1);

        when(content.getContentListByType(Constants.CONTENT_TYPE_IMAGE)).thenReturn(images);

        List<HashMap<String, String>> result = controller.getImages();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).get("id"));
    }

    @Test
    void testGetFiles() {
        List<HashMap<String, String>> files = new ArrayList<>();
        when(content.getContentListByType(Constants.CONTENT_TYPE_FILE)).thenReturn(files);

        List<HashMap<String, String>> result = controller.getFiles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMovies() {
        List<HashMap<String, String>> movies = new ArrayList<>();
        HashMap<String, String> movie1 = new HashMap<>();
        movie1.put("id", "1");
        movie1.put("title", "Movie 1");
        movies.add(movie1);

        when(content.getContentListByType(Constants.CONTENT_TYPE_MOVIE)).thenReturn(movies);

        List<HashMap<String, String>> result = controller.getMovies();

        assertEquals(1, result.size());
    }

    @Test
    void testGetContentsForLink_WithKeyword() {
        List<HashMap<String, String>> results = new ArrayList<>();
        when(content.searchList("updated desc", 1, "", "test")).thenReturn(results);
        when(content.getSearchPager(1, "", "updated desc", "test")).thenReturn("");

        ListBean result = controller.getContentsForLink("", "test", "updated desc", "1");

        assertNotNull(result);
        verify(content).searchList("updated desc", 1, "", "test");
    }

    @Test
    void testGetContentsForLink_WithoutKeyword() {
        List<HashMap<String, String>> results = new ArrayList<>();
        when(content.getList("updated desc", 1, "")).thenReturn(results);
        when(content.getPager(1, "", "updated desc")).thenReturn("");

        ListBean result = controller.getContentsForLink("", "", "updated desc", "1");

        assertNotNull(result);
        verify(content).getList("updated desc", 1, "");
    }

    @Test
    void testGenerateTitle_Success() throws IOException, InterruptedException {
        Map<String, Object> titleResult = new HashMap<>();
        titleResult.put("title", "Generated Title");

        when(openAIService.generateTitle("test input")).thenReturn(titleResult);

        Map<String, Object> result = controller.generateTitle("test input");

        assertNotNull(result);
        assertEquals("Generated Title", result.get("title"));
    }

    @Test
    void testGenerateTitle_Error() throws IOException, InterruptedException {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("error", Map.of("message", "API Error"));

        when(openAIService.generateTitle("test")).thenReturn(errorResult);

        Map<String, Object> result = controller.generateTitle("test");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGenerateTitle_Exception() throws IOException, InterruptedException {
        when(openAIService.generateTitle("test")).thenThrow(new RuntimeException("Test error"));

        Map<String, Object> result = controller.generateTitle("test");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGenerateContent_Success() throws IOException, InterruptedException {
        Map<String, Object> contentResult = new HashMap<>();
        contentResult.put("content", "Generated Content");

        when(openAIService.generateContent("test input")).thenReturn(contentResult);

        Map<String, Object> result = controller.generateContent("test input");

        assertNotNull(result);
        assertEquals("Generated Content", result.get("content"));
    }

    @Test
    void testGenerateContent_Error() throws IOException, InterruptedException {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("error", Map.of("message", "API Error"));

        when(openAIService.generateContent("test")).thenReturn(errorResult);

        Map<String, Object> result = controller.generateContent("test");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGenerateContent_Exception() throws IOException, InterruptedException {
        when(openAIService.generateContent("test")).thenThrow(new RuntimeException("Test error"));

        Map<String, Object> result = controller.generateContent("test");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGetListApi_InvalidPage() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);

        when(content.getList("updated desc", 1, "")).thenReturn(new ArrayList<>());
        when(content.getPager(1, "", "updated desc")).thenReturn("");

        ListBean result = controller.getListApi("", "updated desc", "invalid", "", request, response, session);

        assertNotNull(result);
        verify(content).getList("updated desc", 1, "");
    }

    @Test
    void testGetListApi_EmptyMode() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);

        when(content.getList("updated desc", 1, "")).thenReturn(new ArrayList<>());
        when(content.getPager(1, "", "updated desc")).thenReturn("");

        ListBean result = controller.getListApi("", "updated desc", "1", "", request, response, session);

        assertNotNull(result);
    }

    @Test
    void testGetListApi_WithMode() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);

        when(content.getList("updated desc", 1, Constants.CONTENT_TYPE_IMAGE))
            .thenReturn(new ArrayList<>());
        when(content.getPager(1, Constants.CONTENT_TYPE_IMAGE, "updated desc")).thenReturn("");

        ListBean result = controller.getListApi(Constants.CONTENT_TYPE_IMAGE, "updated desc", "1", "", request, response, session);

        assertNotNull(result);
        verify(content).getList("updated desc", 1, Constants.CONTENT_TYPE_IMAGE);
        verify(content).getPager(1, Constants.CONTENT_TYPE_IMAGE, "updated desc");
    }

    @Test
    void testGetById_StylesheetMode() throws IOException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn(Constants.CONTENT_TYPE_STYLESHEET);
        when(content.doSearch("1")).thenReturn(new HashMap<>());

        ContentBean result = controller.getById("1", Constants.CONTENT_TYPE_STYLESHEET, request, response);

        assertNotNull(result);
        assertEquals(Constants.SCREEN_NAME_STYLESHEET, result.getScreenName());
    }

    @Test
    void testGetById_ScriptMode() throws IOException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn(Constants.CONTENT_TYPE_SCRIPT);
        when(content.doSearch("1")).thenReturn(new HashMap<>());

        ContentBean result = controller.getById("1", Constants.CONTENT_TYPE_SCRIPT, request, response);

        assertNotNull(result);
        assertEquals(Constants.SCREEN_NAME_SCRIPT, result.getScreenName());
    }

    @Test
    void testGetById_ContentMode() throws IOException {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("id", "1");
        contentData.put("title", "Test Title");
        contentData.put("template", "");
        contentData.put("head", "");
        contentData.put("content", "Test Content");
        contentData.put("url", "test/page");
        contentData.put("elementcolor", "");
        contentData.put("schedule_published", "");
        contentData.put("schedule_unpublished", "");
        contentData.put("publishflg_keep", "1");

        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn(Constants.CONTENT_TYPE_CONTENT);
        when(content.doSearch("1")).thenReturn(contentData);
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("1", Constants.CONTENT_TYPE_CONTENT, request, response);

        assertNotNull(result);
        assertEquals(Constants.SCREEN_NAME_CONTENT, result.getScreenName());
    }

    @Test
    void testGetById_NullMode() throws IOException {
        when(request.getParameter("id")).thenReturn("");
        when(request.getParameter("mode")).thenReturn(null);
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("", "", request, response);

        assertNotNull(result);
        assertEquals(Constants.SCREEN_NAME_CONTENT, result.getScreenName());
    }

    @Test
    void testGetById_WithRequestParameters() throws IOException {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("id", "1");
        contentData.put("title", "Test Title");
        contentData.put("template", "");
        contentData.put("head", "");
        contentData.put("content", "Test Content");
        contentData.put("url", "test/page");
        contentData.put("elementcolor", "");
        contentData.put("schedule_published", "");
        contentData.put("schedule_unpublished", "");
        contentData.put("publishflg_keep", "1");

        when(request.getParameter("id")).thenReturn("2");
        when(request.getParameter("mode")).thenReturn("");
        when(content.doSearch("2")).thenReturn(contentData);
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("1", "", request, response);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
    }

    @Test
    void testGetById_TemplateModeWithEmptyContent() throws IOException {
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("id", "1");
        templateData.put("title", "Template");
        templateData.put("content", "");
        templateData.put("head", "");

        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn(Constants.CONTENT_TYPE_TEMPLATE);
        when(content.doSearch("1")).thenReturn(templateData);
        when(setting.elementsColorList()).thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("1", Constants.CONTENT_TYPE_TEMPLATE, request, response);

        assertNotNull(result);
        assertEquals("Template", result.getTitle());
    }

    @Test
    void testGetById_TemplateModeWithNullResult() throws IOException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn(Constants.CONTENT_TYPE_TEMPLATE);
        when(content.doSearch("1")).thenReturn(null);
        when(setting.elementsColorList()).thenReturn(new ArrayList<>());

        ContentBean result = controller.getById("1", Constants.CONTENT_TYPE_TEMPLATE, request, response);

        assertNotNull(result);
    }

    @Test
    void testGetById_ElementModeWithColorElements() throws IOException {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        HashMap<String, String> color1 = new HashMap<>();
        color1.put("name", "header");
        color1.put("code", "#000000");
        colorElements.add(color1);
        HashMap<String, String> color2 = new HashMap<>();
        color2.put("name", "footer");
        color2.put("code", "#FFFFFF");
        colorElements.add(color2);

        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("mode")).thenReturn(Constants.CONTENT_TYPE_ELEMENT);
        when(content.doSearch("1")).thenReturn(new HashMap<>());
        when(setting.elementsColorList()).thenReturn(colorElements);

        ContentBean result = controller.getById("1", Constants.CONTENT_TYPE_ELEMENT, request, response);

        assertNotNull(result);
        assertEquals(Constants.SCREEN_NAME_ELEMENT, result.getScreenName());
        assertTrue(result.getColorOutput().contains("header"));
        assertTrue(result.getColorOutput().contains("footer"));
    }


    @Test
    void testGetContentsForLink_InvalidPage() {
        List<HashMap<String, String>> results = new ArrayList<>();
        when(content.getList("updated desc", 1, "")).thenReturn(results);
        when(content.getPager(1, "", "updated desc")).thenReturn("");

        ListBean result = controller.getContentsForLink("", "", "updated desc", "invalid");

        assertNotNull(result);
        verify(content).getList("updated desc", 1, "");
    }

    @Test
    void testGetListApi_WithLoginName() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn("testuser");
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);

        when(content.getList("updated desc", 1, "")).thenReturn(new ArrayList<>());
        when(content.getPager(1, "", "updated desc")).thenReturn("");

        ListBean result = controller.getListApi("", "updated desc", "1", "", request, response, session);

        assertNotNull(result);
        assertEquals("testuser", result.getLoginName());
        verify(session).setAttribute(Constants.SESSION_LOGIN_NAME, Constants.EMPTY_STRING);
    }

    @Test
    void testGetListApi_WithRegisterMessage() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn("登録しました。");

        when(content.getList("updated desc", 1, "")).thenReturn(new ArrayList<>());
        when(content.getPager(1, "", "updated desc")).thenReturn("");

        ListBean result = controller.getListApi("", "updated desc", "1", "", request, response, session);

        assertNotNull(result);
        assertEquals("登録しました。", result.getRegisterMessage());
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
    }
}
