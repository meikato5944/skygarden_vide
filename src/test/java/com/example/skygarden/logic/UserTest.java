package com.example.skygarden.logic;

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

import com.example.skygarden.config.AppProperties;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Userのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private ContentMapper mapper;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.Pagination pagination;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private User user;

    @BeforeEach
    void setUp() {
        // appPropertiesとpaginationのモックは各テストで必要に応じて設定
    }

    @Test
    void testCreate_Success() throws IOException {
        user.create("testuser", "password", "test@example.com", "1", request, response, session);

        verify(mapper).createUser("testuser", "password", "test@example.com", "1");
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
        verify(response).sendRedirect(Constants.PATH_USER_LIST);
    }

    @Test
    void testCreate_AdminFlagNo() throws IOException {
        user.create("testuser", "password", "test@example.com", "0", request, response, session);

        verify(mapper).createUser("testuser", "password", "test@example.com", Constants.FLAG_NO);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
    }

    @Test
    void testCreate_AdminFlagEmpty() throws IOException {
        user.create("testuser", "password", "test@example.com", "", request, response, session);

        verify(mapper).createUser("testuser", "password", "test@example.com", Constants.FLAG_NO);
    }

    @Test
    void testCreate_AdminFlagNull() throws IOException {
        doNothing().when(response).sendRedirect(anyString());
        
        user.create("testuser", "password", "test@example.com", null, request, response, session);

        verify(mapper).createUser("testuser", "password", "test@example.com", Constants.FLAG_NO);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
        verify(response).sendRedirect(Constants.PATH_USER_LIST);
    }

    @Test
    void testCreate_Exception() throws IOException {
        doThrow(new RuntimeException("DB Error")).when(mapper).createUser(anyString(), anyString(), anyString(), anyString());

        user.create("testuser", "password", "test@example.com", "1", request, response, session);

        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
        verify(response).sendRedirect(Constants.PATH_USER_LIST);
    }

    @Test
    void testCreate_IOException() throws IOException {
        doThrow(new IOException("Redirect Error")).when(response).sendRedirect(anyString());

        user.create("testuser", "password", "test@example.com", "1", request, response, session);

        verify(mapper).createUser(anyString(), anyString(), anyString(), anyString());
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
    }

    @Test
    void testUpdate_Success() throws IOException {
        user.update("1", "testuser", "password", "test@example.com", "1", request, response, session);

        verify(mapper).updateUser("1", "testuser", "password", "test@example.com", "1");
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
        verify(response).sendRedirect(Constants.PATH_USER_LIST);
    }

    @Test
    void testUpdate_AdminFlagNo() throws IOException {
        user.update("1", "testuser", "password", "test@example.com", "0", request, response, session);

        verify(mapper).updateUser("1", "testuser", "password", "test@example.com", Constants.FLAG_NO);
    }

    @Test
    void testUpdate_AdminFlagEmpty() throws IOException {
        user.update("1", "testuser", "password", "test@example.com", "", request, response, session);

        verify(mapper).updateUser("1", "testuser", "password", "test@example.com", Constants.FLAG_NO);
    }

    @Test
    void testUpdate_Exception() throws IOException {
        doThrow(new RuntimeException("DB Error")).when(mapper).updateUser(anyString(), anyString(), anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        user.update("1", "testuser", "password", "test@example.com", "1", request, response, session);

        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
    }

    @Test
    void testGetList_FirstPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        HashMap<String, String> user1 = new HashMap<>();
        user1.put("id", "1");
        user1.put("name", "user1");
        expected.add(user1);
        
        when(mapper.selectAllLimit(eq(Constants.TABLE_USER), eq("id desc"), eq(Constants.EMPTY_STRING), eq(20), eq(0)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = user.getList("id desc", 1);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).get("id"));
    }

    @Test
    void testGetList_SecondPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        when(mapper.selectAllLimit(eq(Constants.TABLE_USER), eq("name"), eq(Constants.EMPTY_STRING), eq(20), eq(20)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = user.getList("name", 2);

        assertNotNull(result);
        verify(mapper).selectAllLimit(Constants.TABLE_USER, "name", Constants.EMPTY_STRING, 20, 20);
    }

    @Test
    void testGetList_ZeroPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        when(mapper.selectAllLimit(eq(Constants.TABLE_USER), eq("id"), eq(Constants.EMPTY_STRING), eq(20), eq(0)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = user.getList("id", 0);

        assertNotNull(result);
    }

    @Test
    void testGetList_NegativePage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        when(mapper.selectAllLimit(eq(Constants.TABLE_USER), eq("id"), eq(Constants.EMPTY_STRING), eq(20), eq(0)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = user.getList("id", -1);

        assertNotNull(result);
    }

    @Test
    void testGetUser_Exists() {
        HashMap<String, String> expected = new HashMap<>();
        expected.put("id", "1");
        expected.put("name", "testuser");
        expected.put("email", "test@example.com");
        expected.put("admin", "1");
        
        when(mapper.getUserById("1")).thenReturn(expected);

        HashMap<String, String> result = user.getUser("1");

        assertNotNull(result);
        assertEquals("1", result.get("id"));
        assertEquals("testuser", result.get("name"));
    }

    @Test
    void testGetUser_NotExists() {
        when(mapper.getUserById("999")).thenReturn(null);

        HashMap<String, String> result = user.getUser("999");

        assertNull(result);
    }

    @Test
    void testGetUser_EmptyId() {
        when(mapper.getUserById("")).thenReturn(null);

        HashMap<String, String> result = user.getUser("");

        assertNull(result);
    }

    @Test
    void testGetPager_FirstPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(50);

        String result = user.getPager(1, "/user-list", "id desc");

        assertNotNull(result);
        assertFalse(result.contains("Previous"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains("Next"));
    }

    @Test
    void testGetPager_MiddlePage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(100);

        String result = user.getPager(3, "/user-list", "name");

        assertNotNull(result);
        assertTrue(result.contains("Previous"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("Next"));
    }

    @Test
    void testGetPager_LastPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(100);

        String result = user.getPager(5, "/user-list", "id");

        assertNotNull(result);
        assertTrue(result.contains("Previous"));
        assertTrue(result.contains("5"));
        assertFalse(result.contains("Next"));
    }

    @Test
    void testGetPager_SinglePage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(10);

        String result = user.getPager(1, "/user-list", "id desc");

        assertNotNull(result);
        assertFalse(result.contains("Previous"));
        assertFalse(result.contains("Next"));
        assertTrue(result.contains("1"));
    }

    @Test
    void testGetPager_ZeroContent() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(0);

        String result = user.getPager(1, "/user-list", "id");

        assertNotNull(result);
        assertFalse(result.contains("Previous"));
        assertFalse(result.contains("Next"));
    }

    @Test
    void testGetPager_ZeroPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(50);

        String result = user.getPager(0, "/user-list", "id");

        assertNotNull(result);
    }

    @Test
    void testGetPager_NegativePage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(50);

        String result = user.getPager(-1, "/user-list", "id");

        assertNotNull(result);
    }

    @Test
    void testGetPager_AllPages() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING)).thenReturn(100);

        String result = user.getPager(1, "/user-list", "id desc");

        // 5ページ分のリンクが含まれることを確認
        for (int i = 1; i <= 5; i++) {
            assertTrue(result.contains(String.valueOf(i)), "Page " + i + " should be in pager");
        }
    }
}
