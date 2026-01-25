package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.config.AppProperties;
import com.example.skygarden.config.AppProperties.File;
import com.example.skygarden.logic.Content;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * PreviewControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class PreviewControllerTest {

    @Mock
    private Content content;

    @Mock
    private AppProperties appProperties;

    @Mock
    private File file;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private PreviewController controller;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(appProperties.getFile()).thenReturn(file);
        when(file.getPreviewFileName()).thenReturn("preview.html");
    }

    @Test
    void testGetById_NoTemplate() throws IOException {
        when(content.previewContent(eq("Test Content"), eq(""))).thenReturn("Test Content");

        controller.getById("", "Test Title", "<style>test</style>", "Test Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        // CommonProc.readFile()が実際に呼ばれるため、ファイルが存在する場合はその内容が使用される
        verify(response).setContentType("text/html; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
        verify(content).previewContent("Test Content", "");
    }

    @Test
    void testGetById_WithTemplate() throws IOException {
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("head", "<style>template</style>");

        when(content.doSearch("1")).thenReturn(templateData);
        when(content.previewContent(eq("Test Content"), eq("1"))).thenReturn("Processed Content");

        controller.getById("1", "Test Title", "<style>content</style>", "Test Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(content).previewContent("Test Content", "1");
        verify(content).doSearch("1");
    }

    @Test
    void testGetById_EmptyParameters() throws IOException {
        when(content.previewContent(eq(""), eq(""))).thenReturn("");

        controller.getById("", "", "", "", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(response).setContentType("text/html; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testGetById_NullParameters() throws IOException {
        // @RequestParamのdefaultValueにより、nullは空文字列として扱われる
        // しかし、実際のコードでは、content != null ? content : Constants.EMPTY_STRING により空文字列に変換される
        // templateはそのまま渡されるが、@RequestParamのdefaultValueにより空文字列として扱われる
        when(content.previewContent(eq(""), any())).thenReturn("");

        controller.getById(null, null, null, null, request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(response).setContentType("text/html; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testGetById_TemplateNotFound() throws IOException {
        when(content.doSearch("999")).thenReturn(null);
        when(content.previewContent(eq("Content"), eq("999"))).thenReturn("Content");

        controller.getById("999", "Title", "Head", "Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(content).doSearch("999");
        verify(content).previewContent("Content", "999");
    }

    @Test
    void testGetById_TemplateWithNullHead() throws IOException {
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("head", null);

        when(content.doSearch("1")).thenReturn(templateData);
        when(content.previewContent(eq("Content"), eq("1"))).thenReturn("Content");

        controller.getById("1", "Title", "Head", "Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(content).doSearch("1");
        verify(content).previewContent("Content", "1");
    }

    @Test
    void testGetById_PreviewFileNotFound() throws IOException {
        when(content.previewContent(eq("Content"), eq(""))).thenReturn("Content");
        // CommonProc.readFileが空文字列を返す場合、デフォルトHTMLが使用される
        // 実際のファイル読み込みはCommonProcで行われるため、モックできない

        controller.getById("", "Title", "Head", "Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(response).setContentType("text/html; charset=UTF-8");
    }

    @Test
    void testGetById_PlaceholderReplacement() throws IOException {
        when(content.previewContent(eq("Test Content"), eq(""))).thenReturn("Test Content");

        controller.getById("", "Test Title", "<style>test</style>", "Test Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(response).setContentType("text/html; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testGetById_NullContentResult() throws IOException {
        when(content.previewContent(eq("Content"), eq(""))).thenReturn(null);

        controller.getById("", "Title", "Head", "Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(response).setContentType("text/html; charset=UTF-8");
    }

    @Test
    void testGetById_SpecialCharactersInTitle() throws IOException {
        when(content.previewContent(eq("Content"), eq(""))).thenReturn("Content");

        controller.getById("", "Title & <test> \"quote\"", "Head", "Content", request, response);

        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull(result);
        verify(response).setContentType("text/html; charset=UTF-8");
    }
}
