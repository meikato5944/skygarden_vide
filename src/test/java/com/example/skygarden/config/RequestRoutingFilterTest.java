package com.example.skygarden.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Content;
import com.example.skygarden.mapper.ContentMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * RequestRoutingFilterのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestRoutingFilterTest {

    @Mock
    private RequestMappingHandlerMapping handlerMapping;

    @Mock
    private ContentMapper mapper;

    @Mock
    private Content content;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.File fileProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter writer;

    @Mock
    private ServletOutputStream outputStream;

    @InjectMocks
    private RequestRoutingFilter filter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        when(appProperties.getFile()).thenReturn(fileProperties);
        when(fileProperties.getUploadDir()).thenReturn(tempDir.resolve("images").toString());
        when(fileProperties.getFileUploadDir()).thenReturn(tempDir.resolve("files").toString());
    }

    @Test
    void testDoFilterInternal_WebAdminPath() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/webadmin/test");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(mapper, never()).searchByUrl(anyString(), anyString());
    }

    @Test
    void testDoFilterInternal_WithHandlerMapping() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(handlerMapping.getHandler(request)).thenReturn(new HandlerExecutionChain(null));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(mapper, never()).searchByUrl(anyString(), anyString());
    }

    @Test
    void testDoFilterInternal_ContentPage() throws Exception {
        // テンプレートファイルを作成
        Path rootPath = tempDir.resolve("root");
        Files.createDirectories(rootPath);
        Path templateFile = rootPath.resolve("original.html");
        Files.write(templateFile, "###title### ###head### ###content###".getBytes());

        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("id", "1");
        contentData.put("type", "");
        contentData.put("title", "Test Title");
        contentData.put("content", "Test Content");

        when(request.getRequestURI()).thenReturn("/test/page");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("test/page", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentData);
        when(content.getTemplateHead("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("<head>");
        when(content.getHead("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("</head>");
        when(content.displayContent("1")).thenReturn("Test Content");
        when(response.getWriter()).thenReturn(writer);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setContentType("text/html; charset=UTF-8");
        verify(writer).write(anyString());
        verify(writer).close();
    }

    @Test
    void testDoFilterInternal_Stylesheet() throws Exception {
        // テンプレートファイルを作成
        Path rootPath = tempDir.resolve("root");
        Files.createDirectories(rootPath);
        Path templateFile = rootPath.resolve("original.stylesheet.html");
        Files.write(templateFile, "###content###".getBytes());

        HashMap<String, String> stylesheetData = new HashMap<>();
        stylesheetData.put("id", "1");
        stylesheetData.put("type", Constants.CONTENT_TYPE_STYLESHEET);
        stylesheetData.put("content", "body { color: red; }");

        when(request.getRequestURI()).thenReturn("/css/style.css");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("css/style.css", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(stylesheetData);
        when(content.getStylesheet("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("body { color: red; }");
        when(response.getWriter()).thenReturn(writer);

        // CommonProc.getRootPath()の戻り値をモックするために、実際のファイルシステムを使用
        // テストでは、テンプレートファイルが存在することを前提とする
        filter.doFilterInternal(request, response, filterChain);

        verify(response).setContentType("text/css");
        verify(writer).write(anyString());
    }

    @Test
    void testDoFilterInternal_Script() throws Exception {
        HashMap<String, String> scriptData = new HashMap<>();
        scriptData.put("id", "1");
        scriptData.put("type", Constants.CONTENT_TYPE_SCRIPT);
        scriptData.put("content", "console.log('test');");

        when(request.getRequestURI()).thenReturn("/js/script.js");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("js/script.js", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(scriptData);
        when(content.getContent("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("console.log('test');");
        when(response.getWriter()).thenReturn(writer);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setContentType("application/javascript");
        verify(writer).write(anyString());
    }

    @Test
    void testDoFilterInternal_Image_FileExists() throws Exception {
        Path imageDir = tempDir.resolve("images");
        Files.createDirectories(imageDir);
        Path imageFile = imageDir.resolve("test-image.jpg");
        Files.write(imageFile, "fake image content".getBytes());

        HashMap<String, String> imageData = new HashMap<>();
        imageData.put("id", "1");
        imageData.put("type", Constants.CONTENT_TYPE_IMAGE);
        imageData.put("content", "test-image.jpg");

        when(request.getRequestURI()).thenReturn("/images/test-image.jpg");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("images/test-image.jpg", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(imageData);
        when(response.getOutputStream()).thenReturn(outputStream);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setContentType(anyString());
        verify(response).setContentLengthLong(anyLong());
        verify(response).setHeader("Cache-Control", "public, max-age=86400");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_Image_FileNotExists() throws Exception {
        HashMap<String, String> imageData = new HashMap<>();
        imageData.put("id", "1");
        imageData.put("type", Constants.CONTENT_TYPE_IMAGE);
        imageData.put("content", "non-existent.jpg");

        when(request.getRequestURI()).thenReturn("/images/non-existent.jpg");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("images/non-existent.jpg", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(imageData);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_File_FileExists() throws Exception {
        Path fileDir = tempDir.resolve("files");
        Files.createDirectories(fileDir);
        Path filePath = fileDir.resolve("test-file.pdf");
        Files.write(filePath, "fake file content".getBytes());

        HashMap<String, String> fileData = new HashMap<>();
        fileData.put("id", "1");
        fileData.put("type", Constants.CONTENT_TYPE_FILE);
        fileData.put("content", "test-file.pdf");
        fileData.put("head", "original-name.pdf");

        when(request.getRequestURI()).thenReturn("/files/test-file.pdf");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("files/test-file.pdf", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(fileData);
        when(response.getOutputStream()).thenReturn(outputStream);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setContentType(anyString());
        verify(response).setContentLengthLong(anyLong());
        verify(response).setHeader(eq("Content-Disposition"), contains("attachment"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_File_FileNotExists() throws Exception {
        HashMap<String, String> fileData = new HashMap<>();
        fileData.put("id", "1");
        fileData.put("type", Constants.CONTENT_TYPE_FILE);
        fileData.put("content", "non-existent.pdf");

        when(request.getRequestURI()).thenReturn("/files/non-existent.pdf");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("files/non-existent.pdf", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(fileData);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NotFound() throws Exception {
        when(request.getRequestURI()).thenReturn("/unknown/page");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("unknown/page", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_HandlerMappingException() throws Exception {
        when(request.getRequestURI()).thenReturn("/test/page");
        when(handlerMapping.getHandler(request)).thenThrow(new RuntimeException("Mapping error"));

        filter.doFilterInternal(request, response, filterChain);

        // 例外が発生しても処理は継続される
        verify(mapper).searchByUrl("test/page", Constants.TABLE_CONTENT_PUBLIC);
    }

    @Test
    void testDoFilterInternal_ContentPageException() throws Exception {
        when(request.getRequestURI()).thenReturn("/test/page");
        when(handlerMapping.getHandler(request)).thenReturn(null);
        when(mapper.searchByUrl("test/page", Constants.TABLE_CONTENT_PUBLIC))
            .thenThrow(new RuntimeException("DB error"));

        filter.doFilterInternal(request, response, filterChain);

        // 例外が発生してもフィルターチェーンは継続される
        verify(filterChain).doFilter(request, response);
    }
}
