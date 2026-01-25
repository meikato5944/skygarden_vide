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
 * FileControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileControllerTest {

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
    private FileController controller;

    @BeforeEach
    void setUp() {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(appProperties.getFile()).thenReturn(fileProperties);
        when(fileProperties.getFileUploadDir()).thenReturn("test-uploads/files");
    }

    @Test
    void testUploadFile_NewFile() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadFile(file, "", "Test File", "test/file.pdf",
                Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                eq("test/file.pdf"), eq("Test File"), eq("test.pdf"), anyString(),
                eq(Constants.CONTENT_TYPE_FILE), anyString(), anyString(),
                anyString(), anyString(), eq(Constants.FLAG_NO));
    }

    @Test
    void testUploadFile_UpdateFile() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old-file.pdf");
        existing.put("head", "old-name.pdf");

        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(existing);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadFile(null, "1", "Updated File", "test/updated.pdf",
                Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).update(eq("1"), anyString(), anyString(),
                eq("test/updated.pdf"), eq("Updated File"), eq("old-name.pdf"), eq("old-file.pdf"),
                eq(Constants.CONTENT_TYPE_FILE), anyString(), anyString(),
                anyString(), anyString(), eq(Constants.FLAG_NO));
    }

    @Test
    void testUploadFile_WithNewFile() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "new-file.pdf", "application/pdf", "test content".getBytes());
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old-file.pdf");
        existing.put("head", "old-name.pdf");

        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(existing);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadFile(file, "1", "Updated File", "test/updated.pdf",
                Constants.FLAG_NO, "", "", request, response, session);

        verify(mapper).update(eq("1"), anyString(), anyString(),
                eq("test/updated.pdf"), eq("Updated File"), eq("new-file.pdf"), anyString(),
                eq(Constants.CONTENT_TYPE_FILE), anyString(), anyString(),
                anyString(), anyString(), eq(Constants.FLAG_NO));
    }

    @Test
    void testUploadFile_Exception() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenThrow(new RuntimeException("DB Error"));
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadFile(file, "", "Test File", "test/file.pdf",
                Constants.FLAG_NO, "", "", request, response, session);

        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), eq(Constants.MESSAGE_REGISTER_FAILED));
    }

    @Test
    void testUploadFile_WithPublished() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadFile(file, "", "Test File", "test/file.pdf",
                Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testUploadFile_UpdateWithPublished() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old-file.pdf");
        existing.put("head", "old-name.pdf");

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

        controller.uploadFile(null, "1", "Updated File", "test/updated.pdf",
                Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testUploadFile_UpdateExistingPublic() throws IOException {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("content", "old-file.pdf");
        existing.put("head", "old-name.pdf");

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

        controller.uploadFile(null, "1", "Updated File", "test/updated.pdf",
                Constants.FLAG_YES, "", "", request, response, session);

        verify(mapper).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testUploadFile_WithScheduleDates() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        controller.uploadFile(file, "", "Test File", "test/file.pdf",
                Constants.FLAG_NO, "2025-12-31 23:59", "2026-01-01 00:00", request, response, session);

        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq("2025-12-31 23:59"), eq("2026-01-01 00:00"), anyString());
    }
}
