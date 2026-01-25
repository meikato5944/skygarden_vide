package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.bean.SettingColorElementsBean;
import com.example.skygarden.logic.Setting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * SettingControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class SettingControllerTest {

    @Mock
    private Setting setting;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private SettingController controller;

    @BeforeEach
    void setUp() {
        // モックは各テストで必要に応じて設定
    }

    @Test
    void testUpdate() throws IOException {
        doNothing().when(setting).doUpdate(any(), any(), any());
        
        controller.update(request, response, session);

        verify(setting).doUpdate(request, response, session);
    }

    @Test
    void testGetSetting_Success() throws IOException {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        HashMap<String, String> color1 = new HashMap<>();
        color1.put("name", "header");
        color1.put("code", "#000000");
        colorElements.add(color1);

        when(setting.elementsColorList()).thenReturn(colorElements);

        SettingColorElementsBean result = controller.getSetting(request, response, session);

        assertNotNull(result);
        assertNotNull(result.getColorElements());
        assertEquals(1, result.getColorElements().size());
        assertEquals("header", result.getColorElements().get(0).get("name"));
        assertEquals("#000000", result.getColorElements().get(0).get("code"));
    }

    @Test
    void testGetSetting_EmptyList() throws IOException {
        when(setting.elementsColorList()).thenReturn(new ArrayList<>());

        SettingColorElementsBean result = controller.getSetting(request, response, session);

        assertNotNull(result);
        assertNotNull(result.getColorElements());
        assertTrue(result.getColorElements().isEmpty());
    }

    @Test
    void testGetSetting_NullList() throws IOException {
        when(setting.elementsColorList()).thenReturn(null);

        SettingColorElementsBean result = controller.getSetting(request, response, session);

        assertNotNull(result);
        assertNull(result.getColorElements());
    }

    @Test
    void testGetSession_Exists() throws IOException {
        when(session.getAttribute("testAttribute")).thenReturn("testValue");

        String result = controller.getUserName("testAttribute", session);

        assertEquals("testValue", result);
    }

    @Test
    void testGetSession_NotExists() throws IOException {
        when(session.getAttribute("nonexistent")).thenReturn(null);

        String result = controller.getUserName("nonexistent", session);

        assertNull(result);
    }

    @Test
    void testGetSession_EmptyString() throws IOException {
        when(session.getAttribute("empty")).thenReturn("");

        String result = controller.getUserName("empty", session);

        assertEquals("", result);
    }

    @Test
    void testGetSession_VariousAttributes() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(session.getAttribute("admin")).thenReturn("1");
        when(session.getAttribute("loginName")).thenReturn("Test User");

        String nameResult = controller.getUserName("name", session);
        String adminResult = controller.getUserName("admin", session);
        String loginNameResult = controller.getUserName("loginName", session);

        assertEquals("testuser", nameResult);
        assertEquals("1", adminResult);
        assertEquals("Test User", loginNameResult);
    }
}
