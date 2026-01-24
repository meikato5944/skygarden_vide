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

import com.example.skygarden.bean.UserBean;
import com.example.skygarden.bean.UserListBean;
import com.example.skygarden.config.AppProperties;
import com.example.skygarden.config.AppProperties.Pagination;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * UserControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private User user;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private AppProperties appProperties;

    @Mock
    private Pagination pagination;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        // モックは各テストで必要に応じて設定
    }

    @Test
    void testUserUpdate_Create() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        when(user.create(anyString(), anyString(), anyString(), anyString(), any(), any(), any())).thenReturn(true);

        controller.userUpdate("", "testuser", "password", "test@example.com", "1", request, response);

        verify(user).create("testuser", "password", "test@example.com", "1", request, response, session);
        verify(user, never()).update(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void testUserUpdate_Update() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        when(user.update(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any(), any())).thenReturn(true);

        controller.userUpdate("1", "testuser", "password", "test@example.com", "1", request, response);

        verify(user).update("1", "testuser", "password", "test@example.com", "1", request, response, session);
        verify(user, never()).create(anyString(), anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void testUserUpdate_EmptyParameters() throws IOException {
        when(request.getSession(true)).thenReturn(session);
        when(user.create(anyString(), anyString(), anyString(), anyString(), any(), any(), any())).thenReturn(true);

        controller.userUpdate("", "", "", "", "", request, response);

        verify(user).create("", "", "", "", request, response, session);
    }

    @Test
    void testGetUser_WithId() throws IOException {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", "testuser");
        userData.put("password", "password123");
        userData.put("email", "test@example.com");
        userData.put("admin", "1");

        when(user.getUser("1")).thenReturn(userData);

        UserBean result = controller.getUser("1", request, response);

        assertNotNull(result);
        assertEquals("testuser", result.getName());
        assertEquals("password123", result.getPassword());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("1", result.getAdmin());
    }

    @Test
    void testGetUser_EmptyId() throws IOException {
        UserBean result = controller.getUser("", request, response);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals("", result.getPassword());
        assertEquals("", result.getEmail());
        assertEquals("", result.getAdmin());
        verify(user, never()).getUser(anyString());
    }

    @Test
    void testGetUser_NotExists() throws IOException {
        when(user.getUser("999")).thenReturn(null);

        UserBean result = controller.getUser("999", request, response);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals("", result.getPassword());
    }

    @Test
    void testGetUser_NullValues() throws IOException {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", null);
        userData.put("password", null);
        userData.put("email", null);
        userData.put("admin", null);

        when(user.getUser("1")).thenReturn(userData);

        UserBean result = controller.getUser("1", request, response);

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getPassword());
    }

    @Test
    void testGetList_Success() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn("testuser");
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn("登録しました。");
        when(request.getParameter("sort")).thenReturn("id desc");
        when(request.getParameter("page")).thenReturn("1");

        List<HashMap<String, String>> userList = new ArrayList<>();
        HashMap<String, String> user1 = new HashMap<>();
        user1.put("id", "1");
        user1.put("name", "user1");
        userList.add(user1);

        when(user.getList("id desc", 1)).thenReturn(userList);
        when(user.getPager(1, Constants.PATH_USER_LIST, "id desc")).thenReturn("<li>1</li>");

        UserListBean result = controller.getList("id desc", "1", request, response, session);

        assertNotNull(result);
        assertEquals("testuser", result.getLoginName());
        assertEquals("登録しました。", result.getRegisterMessage());
        assertEquals(1, result.getResults().size());
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
    }

    @Test
    void testGetList_EmptySort() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("page")).thenReturn(null);

        when(user.getList("", 1)).thenReturn(new ArrayList<>());
        when(user.getPager(1, Constants.PATH_USER_LIST, "")).thenReturn("");

        UserListBean result = controller.getList("", "1", request, response, session);

        assertNotNull(result);
        assertEquals("", result.getLoginName());
        assertEquals("", result.getRegisterMessage());
    }

    @Test
    void testGetList_InvalidPage() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("page")).thenReturn("invalid");

        when(user.getList("", 1)).thenReturn(new ArrayList<>());
        when(user.getPager(1, Constants.PATH_USER_LIST, "")).thenReturn("");

        UserListBean result = controller.getList("", "1", request, response, session);

        assertNotNull(result);
    }

    @Test
    void testGetList_WithSortFromRequest() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(request.getParameter("sort")).thenReturn("name");
        when(request.getParameter("page")).thenReturn("2");

        when(user.getList("name", 2)).thenReturn(new ArrayList<>());
        when(user.getPager(2, Constants.PATH_USER_LIST, "name")).thenReturn("");

        UserListBean result = controller.getList("", "1", request, response, session);

        assertNotNull(result);
        verify(user).getList("name", 2);
    }

    @Test
    void testGetList_WithPageFromRequest() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("page")).thenReturn("3");

        when(user.getList("", 3)).thenReturn(new ArrayList<>());
        when(user.getPager(3, Constants.PATH_USER_LIST, "")).thenReturn("");

        UserListBean result = controller.getList("", "1", request, response, session);

        assertNotNull(result);
        verify(user).getList("", 3);
    }

    @Test
    void testGetList_SortOptions() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn(null);
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("page")).thenReturn(null);

        when(user.getList("id desc", 1)).thenReturn(new ArrayList<>());
        when(user.getPager(1, Constants.PATH_USER_LIST, "id desc")).thenReturn("");

        UserListBean result = controller.getList("id desc", "1", request, response, session);

        assertNotNull(result);
        assertNotNull(result.getSortOutput());
        assertTrue(result.getSortOutput().contains("id desc"));
    }

    @Test
    void testGetList_EmptyLoginName() throws IOException {
        when(session.getAttribute(Constants.SESSION_LOGIN_NAME)).thenReturn("");
        when(session.getAttribute(Constants.SESSION_REGISTER_MESSAGE)).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("page")).thenReturn(null);

        when(user.getList("", 1)).thenReturn(new ArrayList<>());
        when(user.getPager(1, Constants.PATH_USER_LIST, "")).thenReturn("");

        UserListBean result = controller.getList("", "1", request, response, session);

        assertNotNull(result);
        assertEquals("", result.getLoginName());
    }
}
