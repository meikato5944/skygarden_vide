package com.example.skygarden.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.skygarden.constants.Constants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * GlobalExceptionHandlerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        // デフォルトのモック設定
    }

    @Test
    void testHandleMaxSizeException_ImageMode() {
        MaxUploadSizeExceededException exc = new MaxUploadSizeExceededException(1000000);
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/?mode=image");

        String result = handler.handleMaxSizeException(exc, request, session, redirectAttributes);

        assertEquals("redirect:/?mode=image", result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testHandleMaxSizeException_FileMode() {
        MaxUploadSizeExceededException exc = new MaxUploadSizeExceededException(1000000);
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/?mode=file");

        String result = handler.handleMaxSizeException(exc, request, session, redirectAttributes);

        assertEquals("redirect:/?mode=file", result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testHandleMaxSizeException_MovieMode() {
        MaxUploadSizeExceededException exc = new MaxUploadSizeExceededException(1000000);
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/?mode=movie");

        String result = handler.handleMaxSizeException(exc, request, session, redirectAttributes);

        assertEquals("redirect:/?mode=movie", result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testHandleMaxSizeException_NullReferer() {
        MaxUploadSizeExceededException exc = new MaxUploadSizeExceededException(1000000);
        when(request.getHeader("Referer")).thenReturn(null);

        String result = handler.handleMaxSizeException(exc, request, session, redirectAttributes);

        assertEquals("redirect:/?mode=image", result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testHandleMaxSizeException_NoModeInReferer() {
        MaxUploadSizeExceededException exc = new MaxUploadSizeExceededException(1000000);
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/other");

        String result = handler.handleMaxSizeException(exc, request, session, redirectAttributes);

        assertEquals("redirect:/?mode=image", result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testHandleMaxSizeException_ErrorMessageContainsMaxSize() {
        MaxUploadSizeExceededException exc = new MaxUploadSizeExceededException(1000000);
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/?mode=image");

        handler.handleMaxSizeException(exc, request, session, redirectAttributes);

        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), argThat(message -> 
            message != null && message.toString().contains("ファイルサイズが上限")
        ));
    }
}
