package com.example.skygarden.controller;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.skygarden.config.AppProperties;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ImageControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageControllerTest {

    @Mock
    private ContentMapper mapper;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.File fileProperties;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ImageController controller;

    @BeforeEach
    void setUp() {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(appProperties.getFile()).thenReturn(fileProperties);
        when(fileProperties.getUploadDir()).thenReturn("test-uploads/images");
    }

    @Test
    void testUploadImage_NewImage() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadImage(file, "", "Test Image", "test/image.jpg",
                "800", "600", Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                eq("test/image.jpg"), eq("Test Image"), anyString(), anyString(),
                eq(Constants.CONTENT_TYPE_IMAGE), anyString(), anyString(),
                anyString(), anyString(), eq(Constants.FLAG_NO));
    }

    @Test
    void testUploadImage_UpdateImage() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old-file.jpg");

        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(existing);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadImage(null, "1", "Updated Image", "test/updated.jpg",
                "1280", "720", Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).update(eq("1"), anyString(), anyString(),
                eq("test/updated.jpg"), eq("Updated Image"), anyString(), eq("old-file.jpg"),
                eq(Constants.CONTENT_TYPE_IMAGE), anyString(), anyString(),
                anyString(), anyString(), eq(Constants.FLAG_NO));
    }

    @Test
    void testUploadImage_InvalidFileType() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadImage(file, "", "Test", "test",
                "", "", Constants.FLAG_NO, "", "", request, response, session);

        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), contains("画像ファイルのみ"));
        verify(mapper, never()).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testUploadImage_Exception() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenThrow(new RuntimeException("DB Error"));
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadImage(file, "", "Test Image", "test/image.jpg",
                "800", "600", Constants.FLAG_NO, "", "", request, response, session);

        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), eq(Constants.MESSAGE_REGISTER_FAILED));
    }

    @Test
    void testUploadImage_WithPublished() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadImage(file, "", "Test Image", "test/image.jpg",
                "800", "600", Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testUploadImage_UpdateWithPublished() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old-file.jpg");

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

        controller.uploadImage(null, "1", "Updated Image", "test/updated.jpg",
                "1280", "720", Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testUploadImage_UpdateExistingPublic() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old-file.jpg");

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

        controller.uploadImage(null, "1", "Updated Image", "test/updated.jpg",
                "1280", "720", Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testUploadImage_WithScheduleDates() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadImage(file, "", "Test Image", "test/image.jpg",
                "800", "600", Constants.FLAG_NO, "2025-12-31 23:59", "2026-01-01 00:00", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq("2025-12-31 23:59"), eq("2026-01-01 00:00"), anyString());
    }
}
