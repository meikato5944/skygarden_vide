package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * MovieControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MovieControllerTest {

    @Mock
    private ContentMapper mapper;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private MovieController controller;

    @BeforeEach
    void setUp() {
        when(session.getAttribute("name")).thenReturn("testuser");
    }

    @Test
    void testRegisterMovie_NewMovie() throws IOException {
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("", "Test Movie", "https://www.youtube.com/watch?v=test123",
                "800", "600", Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq("test123"),
                eq(Constants.CONTENT_TYPE_MOVIE), anyString(), anyString(),
                anyString(), anyString(), eq(Constants.FLAG_YES));
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testRegisterMovie_UpdateMovie() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old123");
        existing.put("url", "https://www.youtube.com/watch?v=old123");

        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(existing);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("1", "Updated Movie", "https://www.youtube.com/watch?v=new123",
                "1280", "720", Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).update(eq("1"), anyString(), anyString(),
                anyString(), eq("Updated Movie"), anyString(), eq("new123"),
                eq(Constants.CONTENT_TYPE_MOVIE), anyString(), anyString(),
                anyString(), anyString(), eq(Constants.FLAG_YES));
    }

    @Test
    void testRegisterMovie_YoutubeBeUrl() throws IOException {
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("", "Test Movie", "https://youtu.be/test123",
                "800", "600", Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq("test123"),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterMovie_EmbedUrl() throws IOException {
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("", "Test Movie", "https://www.youtube.com/embed/test123",
                "800", "600", Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq("test123"),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterMovie_VideoIdOnly() throws IOException {
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("", "Test Movie", "test12345678",
                "800", "600", Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq("test12345678"),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterMovie_Exception() throws IOException {
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenThrow(new RuntimeException("DB Error"));
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("", "Test Movie", "https://www.youtube.com/watch?v=test123",
                "800", "600", Constants.FLAG_NO, "", "", request, response, session);

        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), eq(Constants.MESSAGE_REGISTER_FAILED));
    }

    @Test
    void testRegisterMovie_UpdateWithPublished() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old123");
        existing.put("url", "https://www.youtube.com/watch?v=old123");

        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(existing);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("1", "Updated Movie", "https://www.youtube.com/watch?v=new123",
                "1280", "720", Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterMovie_UpdateExistingPublic() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old123");
        existing.put("url", "https://www.youtube.com/watch?v=old123");

        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(existing);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");
        doNothing().when(mapper).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("1", "Updated Movie", "https://www.youtube.com/watch?v=new123",
                "1280", "720", Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterMovie_WithScheduleDates() throws IOException {
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("", "Test Movie", "https://www.youtube.com/watch?v=test123",
                "800", "600", Constants.FLAG_NO, "2025-12-31 23:59", "2026-01-01 00:00", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq("2025-12-31 23:59"), eq("2026-01-01 00:00"), anyString());
    }

    @Test
    void testRegisterMovie_EmptyUrl() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old123");
        existing.put("url", "https://www.youtube.com/watch?v=old123");

        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(existing);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        controller.registerMovie("1", "Updated Movie", "",
                "1280", "720", Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq("old123"),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }
}
