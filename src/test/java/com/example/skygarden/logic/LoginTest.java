package com.example.skygarden.logic;

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

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Loginのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class LoginTest {

    @Mock
    private ContentMapper mapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private Login login;

    private HashMap<String, String> userData;

    @BeforeEach
    void setUp() {
        userData = new HashMap<>();
        userData.put("name", "testuser");
        userData.put("password", "testpass");
        userData.put("admin", "1");
    }

    @Test
    void testDoLogin_Success() throws IOException {
        // ユーザーが存在し、パスワードが一致する場合
        when(mapper.getUser("testuser")).thenReturn(userData);

        login.doLogin("testuser", "testpass", request, response, session);

        verify(response).sendRedirect(Constants.PATH_ROOT);
        verify(session).setAttribute(Constants.SESSION_LOGIN_NAME, "testuser");
        verify(session).setAttribute(Constants.SESSION_NAME, "testuser");
        verify(session).setAttribute(Constants.SESSION_ADMIN, "1");
    }

    @Test
    void testDoLogin_UserNotFound() throws IOException {
        // ユーザーが存在しない場合
        when(mapper.getUser("nonexistent")).thenReturn(null);

        login.doLogin("nonexistent", "password", request, response, session);

        verify(response).sendRedirect(Constants.PATH_LOGIN + "?loginError=" + Constants.ERROR_USER_NOT_FOUND);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    void testDoLogin_EmptyUser() throws IOException {
        // ユーザーが空の場合
        when(mapper.getUser("")).thenReturn(null);

        login.doLogin("", "password", request, response, session);

        verify(response).sendRedirect(Constants.PATH_LOGIN + "?loginError=" + Constants.ERROR_USER_NOT_FOUND);
    }

    @Test
    void testDoLogin_PasswordIncorrect() throws IOException {
        // パスワードが一致しない場合
        when(mapper.getUser("testuser")).thenReturn(userData);

        login.doLogin("testuser", "wrongpass", request, response, session);

        verify(response).sendRedirect(Constants.PATH_LOGIN + "?loginError=" + Constants.ERROR_PASSWORD_INCORRECT);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    void testDoLogin_NullPassword() throws IOException {
        // パスワードがnullの場合
        userData.put("password", null);
        when(mapper.getUser("testuser")).thenReturn(userData);

        login.doLogin("testuser", "testpass", request, response, session);

        verify(response).sendRedirect(Constants.PATH_LOGIN + "?loginError=" + Constants.ERROR_PASSWORD_INCORRECT);
    }

    @Test
    void testDoLogin_EmptyPassword() throws IOException {
        // パスワードが空文字列の場合
        userData.put("password", "");
        when(mapper.getUser("testuser")).thenReturn(userData);

        login.doLogin("testuser", "", request, response, session);

        verify(response).sendRedirect(Constants.PATH_ROOT);
        verify(session).setAttribute(Constants.SESSION_LOGIN_NAME, "testuser");
    }
}
