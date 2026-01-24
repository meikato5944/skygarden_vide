package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LoginControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private Login login;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LoginController controller;

    @BeforeEach
    void setUp() {
        // デフォルトのモック設定
    }

    @Test
    void testLogin_Success() throws IOException {
        doNothing().when(login).doLogin(anyString(), anyString(), any(), any(), any());

        controller.login("testuser", "password", request, response, session);

        verify(login).doLogin("testuser", "password", request, response, session);
    }

    @Test
    void testLogin_EmptyName() throws IOException {
        doNothing().when(login).doLogin(anyString(), anyString(), any(), any(), any());

        controller.login("", "password", request, response, session);

        verify(login).doLogin("", "password", request, response, session);
    }

    @Test
    void testLogin_EmptyPassword() throws IOException {
        doNothing().when(login).doLogin(anyString(), anyString(), any(), any(), any());

        controller.login("testuser", "", request, response, session);

        verify(login).doLogin("testuser", "", request, response, session);
    }

    @Test
    void testLogin_BothEmpty() throws IOException {
        doNothing().when(login).doLogin(anyString(), anyString(), any(), any(), any());

        controller.login("", "", request, response, session);

        verify(login).doLogin("", "", request, response, session);
    }

    @Test
    void testLogout() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        doNothing().when(session).invalidate();
        doNothing().when(response).sendRedirect(anyString());

        controller.logout(request, response);

        verify(request).getSession(true);
        verify(session).invalidate();
        verify(response).sendRedirect(Constants.PATH_LOGIN);
    }

    @Test
    void testLogout_IOException() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        doNothing().when(session).invalidate();
        doThrow(new IOException("Redirect error")).when(response).sendRedirect(anyString());

        assertThrows(IOException.class, () -> controller.logout(request, response));
    }

    @Test
    void testAuth_Authenticated() throws IOException {
        when(session.getAttribute(Constants.SESSION_NAME)).thenReturn("testuser");

        boolean result = controller.auth(session);

        assertTrue(result);
    }

    @Test
    void testAuth_NotAuthenticated() throws IOException {
        when(session.getAttribute(Constants.SESSION_NAME)).thenReturn(null);

        boolean result = controller.auth(session);

        assertFalse(result);
    }

    @Test
    void testAuth_EmptyString() throws IOException {
        when(session.getAttribute(Constants.SESSION_NAME)).thenReturn("");

        boolean result = controller.auth(session);

        assertFalse(result);
    }

    @Test
    void testAuth_NullSession() throws IOException {
        when(session.getAttribute(Constants.SESSION_NAME)).thenReturn(null);

        boolean result = controller.auth(session);

        assertFalse(result);
    }
}
