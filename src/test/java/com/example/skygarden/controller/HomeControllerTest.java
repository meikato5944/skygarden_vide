package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Content;
import com.example.skygarden.logic.Setting;
import com.example.skygarden.logic.User;

import jakarta.servlet.http.HttpSession;

/**
 * HomeControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HomeControllerTest {

    @Mock
    private Content content;

    @Mock
    private Setting setting;

    @Mock
    private User user;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private HomeController controller;

    @BeforeEach
    void setUp() {
        // 基本的なモック設定
    }

    @Test
    void testLogin() {
        String result = controller.login();
        assertEquals("login", result);
    }

    @Test
    void testIndex_WithoutKeyword() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(content.getList(anyString(), anyInt(), anyString())).thenReturn(new ArrayList<>());
        when(content.getPager(anyInt(), anyString(), anyString())).thenReturn("");

        String result = controller.index("", "updated desc", "1", "", model, session);

        assertEquals("list", result);
        verify(model).addAttribute("mode", "");
        verify(model).addAttribute("sort", "updated desc");
        verify(model).addAttribute("keyword", "");
    }

    @Test
    void testIndex_WithKeyword() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(content.searchList(anyString(), anyInt(), anyString(), anyString())).thenReturn(new ArrayList<>());
        when(content.getSearchPager(anyInt(), anyString(), anyString(), anyString())).thenReturn("");

        String result = controller.index("", "updated desc", "1", "test", model, session);

        assertEquals("list", result);
        verify(model).addAttribute("keyword", "test");
    }

    @Test
    void testIndex_WithRegisterMessage() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn("登録しました。");
        when(content.getList(anyString(), anyInt(), anyString())).thenReturn(new ArrayList<>());
        when(content.getPager(anyInt(), anyString(), anyString())).thenReturn("");

        String result = controller.index("", "updated desc", "1", "", model, session);

        assertEquals("list", result);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
        verify(model).addAttribute("registerMessage", "登録しました。");
    }

    @Test
    void testIndex_InvalidPage() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(content.getList(anyString(), anyInt(), anyString())).thenReturn(new ArrayList<>());
        when(content.getPager(anyInt(), anyString(), anyString())).thenReturn("");

        String result = controller.index("", "updated desc", "invalid", "", model, session);

        assertEquals("list", result);
        verify(content).getList("updated desc", 1, "");
    }

    @Test
    void testContent_NewContent() {
        when(setting.getDefaultPublishOn()).thenReturn("1");
        when(setting.getAIGenerationVisible()).thenReturn("1");
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        String result = controller.content("", "", model);

        assertEquals("content-edit", result);
        verify(model).addAttribute("id", "");
    }

    @Test
    void testContent_ExistingContent() {
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

        when(content.doSearch("1")).thenReturn(contentData);
        when(setting.getAIGenerationVisible()).thenReturn("1");
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(new ArrayList<>());

        String result = controller.content("", "1", model);

        assertEquals("content-edit", result);
        verify(model).addAttribute("title", "Test Title");
    }

    @Test
    void testContent_TemplateMode() {
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("id", "1");
        templateData.put("content", "###element(10),###content###");

        when(content.doSearch("1")).thenReturn(templateData);
        when(content.doSearch("10")).thenReturn(new HashMap<>());
        when(content.getAllList(anyString(), anyString(), anyString())).thenReturn(new ArrayList<>());
        when(setting.getAIGenerationVisible()).thenReturn("1");

        String result = controller.content(Constants.CONTENT_TYPE_TEMPLATE, "1", model);

        assertEquals("template-edit", result);
    }

    @Test
    void testContent_ElementMode() {
        when(setting.elementsColorList()).thenReturn(new ArrayList<>());
        when(setting.getAIGenerationVisible()).thenReturn("1");

        String result = controller.content(Constants.CONTENT_TYPE_ELEMENT, "", model);

        assertEquals("element-edit", result);
    }

    @Test
    void testContent_ImageMode() {
        HashMap<String, String> imageData = new HashMap<>();
        imageData.put("head", "{\"width\":\"800\",\"height\":\"600\"}");
        imageData.put("schedule_published", "");
        imageData.put("schedule_unpublished", "");
        imageData.put("publishflg_keep", "1");

        when(content.doSearch("1")).thenReturn(imageData);
        when(setting.getAIGenerationVisible()).thenReturn("1");

        String result = controller.content(Constants.CONTENT_TYPE_IMAGE, "1", model);

        assertEquals("image-edit", result);
        verify(model).addAttribute("imageWidth", "800");
        verify(model).addAttribute("imageHeight", "600");
    }

    @Test
    void testContent_FileMode() {
        when(setting.getAIGenerationVisible()).thenReturn("1");

        String result = controller.content(Constants.CONTENT_TYPE_FILE, "", model);

        assertEquals("file-edit", result);
    }

    @Test
    void testContent_MovieMode() {
        HashMap<String, String> movieData = new HashMap<>();
        movieData.put("head", "{\"width\":\"1280\",\"height\":\"720\"}");
        movieData.put("schedule_published", "");
        movieData.put("schedule_unpublished", "");
        movieData.put("publishflg_keep", "1");

        when(content.doSearch("1")).thenReturn(movieData);
        when(setting.getAIGenerationVisible()).thenReturn("1");

        String result = controller.content(Constants.CONTENT_TYPE_MOVIE, "1", model);

        assertEquals("movie-edit", result);
        verify(model).addAttribute("movieWidth", "1280");
        verify(model).addAttribute("movieHeight", "720");
    }

    @Test
    void testUserList_WithoutRegisterMessage() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(user.getList(anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(user.getPager(anyInt(), anyString(), anyString())).thenReturn("");

        String result = controller.userList("updated desc", "1", model, session);

        assertEquals("user-list", result);
        verify(model).addAttribute("registerMessage", "");
    }

    @Test
    void testUserList_WithRegisterMessage() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn("登録しました。");
        when(user.getList(anyString(), anyInt())).thenReturn(new ArrayList<>());
        when(user.getPager(anyInt(), anyString(), anyString())).thenReturn("");

        String result = controller.userList("updated desc", "1", model, session);

        assertEquals("user-list", result);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
    }

    @Test
    void testUser_NewUser() {
        String result = controller.user("", model);

        assertEquals("user", result);
        verify(model).addAttribute("id", "");
        verify(model).addAttribute("name", Constants.EMPTY_STRING);
    }

    @Test
    void testUser_ExistingUser() {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", "testuser");
        userData.put("password", "password");
        userData.put("email", "test@example.com");
        userData.put("admin", Constants.FLAG_YES);

        when(user.getUser("1")).thenReturn(userData);

        String result = controller.user("1", model);

        assertEquals("user", result);
        verify(model).addAttribute("name", "testuser");
        verify(model).addAttribute("email", "test@example.com");
    }

    @Test
    void testSetting() {
        when(setting.elementsColorList()).thenReturn(new ArrayList<>());
        when(setting.getDefaultPublishOn()).thenReturn("1");
        when(setting.getOpenAISetting(anyString())).thenReturn("");
        when(setting.getAIGenerationVisible()).thenReturn("1");
        when(setting.getEmailSetting(anyString())).thenReturn("");

        String result = controller.setting(model);

        assertEquals("setting", result);
        verify(model).addAttribute(eq("colorElements"), anyList());
    }

    @Test
    void testUrlDirectory() {
        when(content.getUrlDirectoryTree(anyString())).thenReturn(new ArrayList<>());

        String result = controller.urlDirectory(model);

        assertEquals("url-directory", result);
        verify(model).addAttribute(eq("contentTree"), anyList());
        verify(model).addAttribute(eq("templateTree"), anyList());
        verify(model).addAttribute(eq("imageTree"), anyList());
        verify(model).addAttribute(eq("fileTree"), anyList());
        verify(model).addAttribute(eq("cssTree"), anyList());
        verify(model).addAttribute(eq("jsTree"), anyList());
        verify(model).addAttribute(eq("movieTree"), anyList());
    }

    @Test
    void testContent_Exception() {
        when(setting.getDefaultPublishOn()).thenReturn("1");
        when(setting.getAIGenerationVisible()).thenReturn("1");
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenThrow(new RuntimeException("DB Error"));

        String result = controller.content("", "", model);

        assertEquals("content-edit", result);
        verify(model, atLeastOnce()).addAttribute(eq("id"), anyString());
    }

    @Test
    void testContent_WithExistingContentAndTemplate() {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("id", "1");
        contentData.put("title", "Test Title");
        contentData.put("template", "2");
        contentData.put("head", "");
        contentData.put("content", "Test Content");
        contentData.put("url", "test/page");
        contentData.put("elementcolor", "");
        contentData.put("schedule_published", "");
        contentData.put("schedule_unpublished", "");
        contentData.put("publishflg_keep", "1");

        List<HashMap<String, String>> templates = new ArrayList<>();
        HashMap<String, String> template1 = new HashMap<>();
        template1.put("id", "2");
        template1.put("title", "Template 2");
        templates.add(template1);

        when(content.doSearch("1")).thenReturn(contentData);
        when(setting.getAIGenerationVisible()).thenReturn("1");
        when(content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE))
            .thenReturn(templates);

        String result = controller.content("", "1", model);

        assertEquals("content-edit", result);
        verify(model).addAttribute("title", "Test Title");
    }

    @Test
    void testIndex_Exception() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(content.getList(anyString(), anyInt(), anyString())).thenThrow(new RuntimeException("DB Error"));

        String result = controller.index("", "updated desc", "1", "", model, session);

        assertEquals("list", result);
        verify(model).addAttribute("screenName", Constants.SCREEN_NAME_CONTENT);
    }

    @Test
    void testUserList_Exception() {
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(user.getList(anyString(), anyInt())).thenThrow(new RuntimeException("DB Error"));

        String result = controller.userList("updated desc", "1", model, session);

        assertEquals("user-list", result);
        verify(model).addAttribute("registerMessage", "");
    }

    @Test
    void testUser_Exception() {
        when(user.getUser("1")).thenThrow(new RuntimeException("DB Error"));

        String result = controller.user("1", model);

        assertEquals("user", result);
        verify(model).addAttribute("id", "1");
        verify(model).addAttribute("name", Constants.EMPTY_STRING);
    }

    @Test
    void testSetting_Exception() {
        when(setting.elementsColorList()).thenThrow(new RuntimeException("DB Error"));
        when(setting.getDefaultPublishOn()).thenReturn("1");
        when(setting.getOpenAISetting(anyString())).thenReturn("");
        when(setting.getAIGenerationVisible()).thenReturn("1");
        when(setting.getEmailSetting(anyString())).thenReturn("");

        String result = controller.setting(model);

        assertEquals("setting", result);
        verify(model).addAttribute(eq("colorElements"), anyList());
    }

    @Test
    void testUrlDirectory_Exception() {
        when(content.getUrlDirectoryTree(anyString())).thenThrow(new RuntimeException("DB Error"));

        String result = controller.urlDirectory(model);

        assertEquals("url-directory", result);
        verify(model).addAttribute(eq("contentTree"), anyList());
    }
}
