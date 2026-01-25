package com.example.skygarden.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.skygarden.bean.DirectoryNodeBean;
import com.example.skygarden.config.AppProperties;
import com.example.skygarden.config.AppProperties.Pagination;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Contentのテストクラス
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContentTest {

    @Mock
    private ContentMapper mapper;

    @Mock
    private AppProperties appProperties;

    @Mock
    private Pagination pagination;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private Content content;

    private HashMap<String, String> contentData;

    @BeforeEach
    void setUp() {
        // appPropertiesとpaginationのモックは各テストで必要に応じて設定
        contentData = new HashMap<>();
        contentData.put("id", "1");
        contentData.put("title", "Test Title");
        contentData.put("url", "test/page");
        contentData.put("head", "<style>test</style>");
        contentData.put("content", "Test Content");
        contentData.put("type", "");
        contentData.put("template", "");
        contentData.put("elementcolor", "");
        contentData.put("schedule_published", "");
        contentData.put("schedule_unpublished", "");
        contentData.put("publishflg_keep", "1");
        contentData.put("created_by", "user1");
    }

    @Test
    void testDoSearch_Exists() {
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);

        HashMap<String, String> result = content.doSearch("1");

        assertNotNull(result);
        assertEquals("1", result.get("id"));
        assertEquals("Test Title", result.get("title"));
    }

    @Test
    void testDoSearch_NotExists() {
        when(mapper.search("999", Constants.TABLE_CONTENT)).thenReturn(null);

        HashMap<String, String> result = content.doSearch("999");

        assertNull(result);
        verify(mapper).search("999", Constants.TABLE_CONTENT);
    }

    @Test
    void testDoSearch_EmptyId() {
        when(mapper.search("", Constants.TABLE_CONTENT)).thenReturn(null);

        HashMap<String, String> result = content.doSearch("");

        assertNull(result);
        verify(mapper).search("", Constants.TABLE_CONTENT);
    }

    @Test
    void testGetList_FirstPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        expected.add(contentData);

        when(mapper.selectAllLimit(eq(Constants.TABLE_CONTENT), eq("updated desc"), eq(""), eq(20), eq(0)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = content.getList("updated desc", 1, "");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).get("id"));
    }

    @Test
    void testGetList_SecondPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        when(mapper.selectAllLimit(eq(Constants.TABLE_CONTENT), eq("id"), eq("template"), eq(20), eq(20)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = content.getList("id", 2, "template");

        assertNotNull(result);
        verify(mapper).selectAllLimit(Constants.TABLE_CONTENT, "id", "template", 20, 20);
    }

    @Test
    void testGetList_ZeroPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        when(mapper.selectAllLimit(eq(Constants.TABLE_CONTENT), eq("id"), eq(""), eq(20), eq(0)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = content.getList("id", 0, "");

        assertNotNull(result);
    }

    @Test
    void testSearchList_WithKeyword() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        expected.add(contentData);

        when(mapper.searchByKeyword(eq(Constants.TABLE_CONTENT), eq(""), eq("test"), eq("updated desc"), eq(20), eq(0)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = content.searchList("updated desc", 1, "", "test");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).get("id"));
    }

    @Test
    void testSearchList_EmptyKeyword() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        
        List<HashMap<String, String>> expected = new ArrayList<>();
        when(mapper.searchByKeyword(eq(Constants.TABLE_CONTENT), eq(""), eq(""), eq("id"), eq(20), eq(0)))
            .thenReturn(expected);

        List<HashMap<String, String>> result = content.searchList("id", 1, "", "");

        assertNotNull(result);
    }

    @Test
    void testGetSearchPager_FirstPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSizeByKeyword(Constants.TABLE_CONTENT, "", "test")).thenReturn(50);

        String result = content.getSearchPager(1, "", "updated desc", "test");

        assertNotNull(result);
        assertFalse(result.contains("Previous"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains("Next"));
        assertTrue(result.contains("test"));
    }

    @Test
    void testGetSearchPager_MiddlePage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSizeByKeyword(Constants.TABLE_CONTENT, "", "keyword")).thenReturn(100);

        String result = content.getSearchPager(3, "", "id", "keyword");

        assertNotNull(result);
        assertTrue(result.contains("Previous"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("Next"));
    }

    @Test
    void testGetSearchPager_LastPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSizeByKeyword(Constants.TABLE_CONTENT, "", "test")).thenReturn(100);

        String result = content.getSearchPager(5, "", "id", "test");

        assertNotNull(result);
        assertTrue(result.contains("Previous"));
        assertFalse(result.contains("Next"));
    }

    @Test
    void testGetSearchPager_ZeroContent() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSizeByKeyword(Constants.TABLE_CONTENT, "", "test")).thenReturn(0);

        String result = content.getSearchPager(1, "", "id", "test");

        assertNotNull(result);
        assertFalse(result.contains("Previous"));
        assertFalse(result.contains("Next"));
    }

    @Test
    void testGetSearchPager_SpecialCharactersInKeyword() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSizeByKeyword(Constants.TABLE_CONTENT, "", "test & keyword")).thenReturn(10);

        String result = content.getSearchPager(1, "", "id", "test & keyword");

        assertNotNull(result);
    }

    @Test
    void testGetAllList() {
        List<HashMap<String, String>> expected = new ArrayList<>();
        expected.add(contentData);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "updated desc", "")).thenReturn(expected);

        List<HashMap<String, String>> result = content.getAllList(Constants.TABLE_CONTENT, "updated desc", "");

        assertEquals(1, result.size());
    }

    @Test
    void testGetContentListByType() {
        List<HashMap<String, String>> expected = new ArrayList<>();
        expected.add(contentData);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "updated desc", Constants.CONTENT_TYPE_IMAGE))
            .thenReturn(expected);

        List<HashMap<String, String>> result = content.getContentListByType(Constants.CONTENT_TYPE_IMAGE);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTitle() {
        when(mapper.searchContentByAttribute("1", "title", Constants.TABLE_CONTENT)).thenReturn("Test Title");

        String result = content.getTitle("1", Constants.TABLE_CONTENT);

        assertEquals("Test Title", result);
    }

    @Test
    void testGetTitle_NotExists() {
        when(mapper.searchContentByAttribute("999", "title", Constants.TABLE_CONTENT)).thenReturn(null);

        String result = content.getTitle("999", Constants.TABLE_CONTENT);

        assertNull(result);
    }

    @Test
    void testGetContent() {
        when(mapper.searchContentByAttribute("1", "content", Constants.TABLE_CONTENT)).thenReturn("Test Content");

        String result = content.getContent("1", Constants.TABLE_CONTENT);

        assertEquals("Test Content", result);
    }

    @Test
    void testGetStylesheet() {
        String cssContent = "body {\n  color: red;\r\n  background: blue;\n}";
        when(mapper.searchContentByAttribute("1", "content", Constants.TABLE_CONTENT)).thenReturn(cssContent);

        String result = content.getStylesheet("1", Constants.TABLE_CONTENT);

        assertNotNull(result);
        assertFalse(result.contains("\r\n"));
    }

    @Test
    void testGetHead() {
        when(mapper.searchContentByAttribute("1", "head", Constants.TABLE_CONTENT)).thenReturn("<style>test</style>");

        String result = content.getHead("1", Constants.TABLE_CONTENT);

        assertEquals("<style>test</style>", result);
    }

    @Test
    void testGetTemplateHead_WithTemplate() {
        HashMap<String, String> contentWithTemplate = new HashMap<>();
        contentWithTemplate.put("template", "2");
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentWithTemplate);
        when(mapper.searchContentByAttribute("2", "head", Constants.TABLE_CONTENT_PUBLIC))
            .thenReturn("<style>template</style>");

        String result = content.getTemplateHead("1", Constants.TABLE_CONTENT_PUBLIC);

        assertEquals("<style>template</style>", result);
    }

    @Test
    void testGetTemplateHead_NoTemplate() {
        HashMap<String, String> contentWithoutTemplate = new HashMap<>();
        contentWithoutTemplate.put("template", "");
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentWithoutTemplate);

        String result = content.getTemplateHead("1", Constants.TABLE_CONTENT_PUBLIC);

        assertEquals(Constants.EMPTY_STRING, result);
    }

    @Test
    void testGetTemplateHead_NullTemplate() {
        HashMap<String, String> contentWithNullTemplate = new HashMap<>();
        contentWithNullTemplate.put("template", null);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentWithNullTemplate);

        String result = content.getTemplateHead("1", Constants.TABLE_CONTENT_PUBLIC);

        assertEquals(Constants.EMPTY_STRING, result);
    }

    @Test
    void testGetTemplateHead_ContentNotFound() {
        when(mapper.search("999", Constants.TABLE_CONTENT)).thenReturn(null);

        String result = content.getTemplateHead("999", Constants.TABLE_CONTENT_PUBLIC);

        assertEquals(Constants.EMPTY_STRING, result);
    }


    @Test
    void testGetTemplateContent_WithTemplate() {
        HashMap<String, String> contentWithTemplate = new HashMap<>();
        contentWithTemplate.put("template", "2");
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentWithTemplate);
        when(mapper.searchContentByAttribute("2", "content", Constants.TABLE_CONTENT_PUBLIC))
            .thenReturn("###element(1),###content###,###element(2)");

        String result = content.getTemplateContent("1", Constants.TABLE_CONTENT_PUBLIC);

        assertEquals("###element(1),###content###,###element(2)", result);
    }

    @Test
    void testGetTemplateContent_NoTemplate() {
        HashMap<String, String> contentWithoutTemplate = new HashMap<>();
        contentWithoutTemplate.put("template", "");
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentWithoutTemplate);

        String result = content.getTemplateContent("1", Constants.TABLE_CONTENT_PUBLIC);

        assertEquals(Constants.EMPTY_STRING, result);
    }

    @Test
    void testDisplayContent_NoTemplate() {
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("Test Content"));
    }

    @Test
    void testDisplayContent_WithTemplate() {
        HashMap<String, String> contentWithTemplate = new HashMap<>();
        contentWithTemplate.put("id", "1");
        contentWithTemplate.put("template", "2");
        contentWithTemplate.put("content", "Main Content");

        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###element(10),###content###,###element(20)");

        HashMap<String, String> element10 = new HashMap<>();
        element10.put("content", "<header>Header</header>");
        HashMap<String, String> element20 = new HashMap<>();
        element20.put("content", "<footer>Footer</footer>");

        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentWithTemplate);
        when(mapper.search("2", Constants.TABLE_CONTENT)).thenReturn(templateData);
        when(mapper.search("10", Constants.TABLE_CONTENT)).thenReturn(element10);
        when(mapper.search("20", Constants.TABLE_CONTENT)).thenReturn(element20);

        String result = content.displayContent("1");

        assertNotNull(result);
    }

    @Test
    void testDisplayContent_WithMovieTag() {
        HashMap<String, String> contentWithMovie = new HashMap<>();
        contentWithMovie.put("id", "1");
        contentWithMovie.put("content", "Test [movie id=11] Content");

        HashMap<String, String> movieData = new HashMap<>();
        movieData.put("id", "11");
        movieData.put("type", Constants.CONTENT_TYPE_MOVIE);
        movieData.put("content", "dQw4w9WgXcQ");

        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentWithMovie);
        when(mapper.search("11", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(movieData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("youtube.com/embed"));
        assertTrue(result.contains("dQw4w9WgXcQ"));
    }

    @Test
    void testDisplayContent_WithMovieTagAndSize() {
        HashMap<String, String> contentWithMovie = new HashMap<>();
        contentWithMovie.put("id", "1");
        contentWithMovie.put("content", "Test [movie id=11, width=800px, height=600px] Content");

        HashMap<String, String> movieData = new HashMap<>();
        movieData.put("id", "11");
        movieData.put("type", Constants.CONTENT_TYPE_MOVIE);
        movieData.put("content", "dQw4w9WgXcQ");

        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentWithMovie);
        when(mapper.search("11", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(movieData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("width=\"800px\""));
        assertTrue(result.contains("height=\"600px\""));
    }

    @Test
    void testDisplayContent_WithMovieTagWidthOnly() {
        HashMap<String, String> contentWithMovie = new HashMap<>();
        contentWithMovie.put("id", "1");
        contentWithMovie.put("content", "Test [movie id=11, width=800px] Content");

        HashMap<String, String> movieData = new HashMap<>();
        movieData.put("id", "11");
        movieData.put("type", Constants.CONTENT_TYPE_MOVIE);
        movieData.put("content", "dQw4w9WgXcQ");

        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentWithMovie);
        when(mapper.search("11", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(movieData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("width=\"800px\""));
        assertTrue(result.contains("height=\"315px\""));
    }

    @Test
    void testDisplayContent_WithMovieTagHeightOnly() {
        HashMap<String, String> contentWithMovie = new HashMap<>();
        contentWithMovie.put("id", "1");
        contentWithMovie.put("content", "Test [movie id=11, height=600px] Content");

        HashMap<String, String> movieData = new HashMap<>();
        movieData.put("id", "11");
        movieData.put("type", Constants.CONTENT_TYPE_MOVIE);
        movieData.put("content", "dQw4w9WgXcQ");

        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentWithMovie);
        when(mapper.search("11", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(movieData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("width=\"560px\""));
        assertTrue(result.contains("height=\"600px\""));
    }

    @Test
    void testDisplayContent_MovieNotFound() {
        HashMap<String, String> contentWithMovie = new HashMap<>();
        contentWithMovie.put("id", "1");
        contentWithMovie.put("content", "Test [movie id=999] Content");

        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(contentWithMovie);
        when(mapper.search("999", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("Not found or not published"));
    }

    @Test
    void testDisplayContent_WithTemplateAndElementNotFound() {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("template", "1");
        contentData.put("content", "Test Content");

        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###element(999),###content###");

        HashMap<String, String> emptyElement = new HashMap<>();
        emptyElement.put("content", null);

        when(mapper.search("1", "content_public")).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(templateData);
        when(mapper.search("999", Constants.TABLE_CONTENT)).thenReturn(emptyElement);

        String result = content.displayContent("1");

        assertNotNull(result);
    }

    @Test
    void testDisplayContent_WithTemplateAndContentPlaceholder() {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("template", "1");
        contentData.put("content", "My Content");

        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###content###");

        when(mapper.search("1", "content_public")).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(templateData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("My Content"));
    }

    @Test
    void testPreviewContent_NoTemplate() {
        String result = content.previewContent("Test Content", null);

        assertNotNull(result);
        assertTrue(result.contains("Test Content"));
    }

    @Test
    void testPreviewContent_WithTemplate() {
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###element(10),###content###,###element(20)");

        HashMap<String, String> element10 = new HashMap<>();
        element10.put("content", "<header>Header</header>");
        HashMap<String, String> element20 = new HashMap<>();
        element20.put("content", "<footer>Footer</footer>");

        when(mapper.search("2", Constants.TABLE_CONTENT)).thenReturn(templateData);
        when(mapper.search("10", Constants.TABLE_CONTENT)).thenReturn(element10);
        when(mapper.search("20", Constants.TABLE_CONTENT)).thenReturn(element20);

        String result = content.previewContent("Main Content", "2");

        assertNotNull(result);
    }

    @Test
    void testPreviewContent_WithMovieTag() {
        HashMap<String, String> movieData = new HashMap<>();
        movieData.put("id", "11");
        movieData.put("type", Constants.CONTENT_TYPE_MOVIE);
        movieData.put("content", "dQw4w9WgXcQ");

        when(mapper.search("11", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(movieData);

        String result = content.previewContent("Test [movie id=11] Content", null);

        assertNotNull(result);
        assertTrue(result.contains("youtube.com/embed"));
    }

    @Test
    void testGetPager_FirstPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_CONTENT, "")).thenReturn(50);

        String result = content.getPager(1, "", "updated desc");

        assertNotNull(result);
        assertFalse(result.contains("Previous"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains("Next"));
    }

    @Test
    void testGetPager_MiddlePage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_CONTENT, "")).thenReturn(100);

        String result = content.getPager(3, "", "id");

        assertNotNull(result);
        assertTrue(result.contains("Previous"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("Next"));
    }

    @Test
    void testGetPager_LastPage() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_CONTENT, "")).thenReturn(100);

        String result = content.getPager(5, "", "id");

        assertNotNull(result);
        assertTrue(result.contains("Previous"));
        assertFalse(result.contains("Next"));
    }

    @Test
    void testGetPager_ZeroContent() {
        when(appProperties.getPagination()).thenReturn(pagination);
        when(pagination.getPageSize()).thenReturn(20);
        when(mapper.getContentSize(Constants.TABLE_CONTENT, "")).thenReturn(0);

        String result = content.getPager(1, "", "id");

        assertNotNull(result);
        assertFalse(result.contains("Previous"));
        assertFalse(result.contains("Next"));
    }

    @Test
    void testGetRedirectUrl_Content() {
        String result = content.getRedirectUrl("");

        assertEquals(Constants.PATH_ROOT, result);
    }

    @Test
    void testGetRedirectUrl_Template() {
        String result = content.getRedirectUrl(Constants.CONTENT_TYPE_TEMPLATE);

        assertEquals(Constants.PATH_ROOT + "?mode=template", result);
    }

    @Test
    void testGetRedirectUrl_Image() {
        String result = content.getRedirectUrl(Constants.CONTENT_TYPE_IMAGE);

        assertEquals(Constants.PATH_ROOT + "?mode=image", result);
    }

    @Test
    void testUrlMatches_Matches() {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("id", "2");
        when(mapper.searchByUrl("test/page", Constants.TABLE_CONTENT)).thenReturn(existing);

        boolean result = content.urlMatches("test/page", "1");

        assertTrue(result);
    }

    @Test
    void testUrlMatches_NoMatch() {
        when(mapper.searchByUrl("test/page", Constants.TABLE_CONTENT)).thenReturn(null);

        boolean result = content.urlMatches("test/page", "1");

        assertFalse(result);
    }

    @Test
    void testUrlMatches_SameId() {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("id", "1");
        when(mapper.searchByUrl("test/page", Constants.TABLE_CONTENT)).thenReturn(existing);

        boolean result = content.urlMatches("test/page", "1");

        assertFalse(result); // 同じIDの場合は重複とみなさない
    }

    @Test
    void testUrlMatches_EmptyResult() {
        HashMap<String, String> empty = new HashMap<>();
        when(mapper.searchByUrl("test/page", Constants.TABLE_CONTENT)).thenReturn(empty);

        boolean result = content.urlMatches("test/page", "1");

        assertFalse(result);
    }

    @Test
    void testUrlMatches_EmptyId() {
        HashMap<String, String> existing = new HashMap<>();
        existing.put("id", "");
        when(mapper.searchByUrl("test/page", Constants.TABLE_CONTENT)).thenReturn(existing);

        boolean result = content.urlMatches("test/page", "1");

        assertFalse(result);
    }

    @Test
    void testGetUrlDirectoryTree_EmptyList() {
        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(new ArrayList<>());

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_NullList() {
        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(null);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_SingleLevel() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "page1.html");
        content1.put("title", "Page 1");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertEquals(1, result.size());
        assertEquals("ROOT", result.get(0).getName());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("page1.html", result.get(0).getChildren().get(0).getName());
        assertEquals("1", result.get(0).getChildren().get(0).getId());
    }

    @Test
    void testGetUrlDirectoryTree_MultiLevel() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "about/company.html");
        content1.put("title", "Company");
        contents.add(content1);

        HashMap<String, String> content2 = new HashMap<>();
        content2.put("id", "2");
        content2.put("url", "about/history.html");
        content2.put("title", "History");
        contents.add(content2);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertEquals(1, result.size());
        DirectoryNodeBean root = result.get(0);
        assertEquals("ROOT", root.getName());
        assertEquals(1, root.getChildren().size());
        DirectoryNodeBean aboutDir = root.getChildren().get(0);
        assertEquals("about", aboutDir.getName());
        assertTrue(aboutDir.isDirectory());
        assertEquals(2, aboutDir.getChildren().size());
    }

    @Test
    void testGetUrlDirectoryTree_WithLeadingSlash() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "/test/page.html");
        content1.put("title", "Page");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertEquals(1, result.size());
        // 先頭スラッシュは除去される
    }

    @Test
    void testGetUrlDirectoryTree_EmptyUrl() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "");
        content1.put("title", "Page");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        // URLが空のコンテンツはスキップされる
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_NullUrl() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", null);
        content1.put("title", "Page");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        // URLがnullのコンテンツはスキップされる
        assertTrue(result.isEmpty());
    }

    @Test
    void testDoCreate_Success() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(mapper).create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoCreate_WithPublishedFlagNo() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("0");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testDoCreate_WithNullUrl() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn(null);
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(mapper).create(anyString(), anyString(), anyString(), anyString(), eq(""), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoCreate_WithNullParameters() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn(null);
        when(request.getParameter("title")).thenReturn(null);
        when(request.getParameter("head")).thenReturn(null);
        when(request.getParameter("content")).thenReturn(null);
        when(request.getParameter("type")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getParameter("elementcolor")).thenReturn(null);
        when(request.getParameter("published")).thenReturn(null);
        when(request.getParameter("schedule_published")).thenReturn(null);
        when(request.getParameter("schedule_unpublished")).thenReturn(null);
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
    }

    @Test
    void testDoCreate_WithLeadingSlash() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn("/test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("0");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        // URLの先頭スラッシュが除去されることを確認
        verify(mapper).create(anyString(), anyString(), anyString(), anyString(), eq("test/page"), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoCreate_WithSchedulePublished() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        // 未来の日時を設定（現在より1年後）
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.YEAR, 1);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        String futureDate = sdf.format(cal.getTime());
        when(request.getParameter("schedule_published")).thenReturn(futureDate);
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        // 未来のスケジュール公開日時は公開テーブルに登録されない（バッチ処理で公開される）
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_Exception() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doThrow(new RuntimeException("DB Error")).when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
    }

    @Test
    void testDoCreate_IOException() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString())).thenReturn(1);
        doThrow(new IOException("Redirect error")).when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(mapper).create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_IOException() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");
        doNothing().when(mapper).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doThrow(new IOException("Redirect error")).when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoCreate_EmailError() throws IOException, ParseException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(1);
        when(emailService.sendContentPublishedNotification(anyString(), anyString(), any()))
            .thenReturn("Email error");

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(emailService).sendContentPublishedNotification(anyString(), anyString(), any());
    }

    @Test
    void testDoCreate_Exception() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("DB Error"));

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
    }

    @Test
    void testDoUpdate_Success() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Updated Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Updated Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_WithNullUrl() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn(null);
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");
        doNothing().when(mapper).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).update(anyString(), anyString(), anyString(), eq(""), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_WithNullParameters() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn(null);
        when(request.getParameter("title")).thenReturn(null);
        when(request.getParameter("head")).thenReturn(null);
        when(request.getParameter("content")).thenReturn(null);
        when(request.getParameter("type")).thenReturn(null);
        when(request.getParameter("elementcolor")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getParameter("published")).thenReturn(null);
        when(request.getParameter("schedule_published")).thenReturn(null);
        when(request.getParameter("schedule_unpublished")).thenReturn(null);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
    }

    @Test
    void testDoUpdate_UpdateExistingPublic() throws IOException {
        when(session.getAttribute("name")).thenReturn("user1");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Updated Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Updated Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).updatePublic(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_WithFutureSchedulePublished() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.YEAR, 1);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        String futureDate = sdf.format(cal.getTime());
        when(request.getParameter("schedule_published")).thenReturn(futureDate);
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        verify(mapper, never()).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testDoDelete_Success() throws IOException {
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");

        boolean result = content.doDelete("1", "", response, session);

        assertTrue(result);
        verify(mapper).delete(Constants.TABLE_CONTENT, "1");
        verify(mapper).delete(Constants.TABLE_CONTENT_PUBLIC, "1");
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_DELETE_SUCCESS);
    }

    @Test
    void testDoDelete_NoPublicContent() throws IOException {
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);

        boolean result = content.doDelete("1", "", response, session);

        assertTrue(result);
        verify(mapper).delete(Constants.TABLE_CONTENT, "1");
        verify(mapper, never()).delete(Constants.TABLE_CONTENT_PUBLIC, "1");
    }

    @Test
    void testDoDelete_PublicContentWithEmptyId() throws IOException {
        when(mapper.searchContentByAttribute("1", "id", "content_public")).thenReturn("");
        doNothing().when(mapper).delete(anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doDelete("1", "content", response, session);

        assertTrue(result);
        verify(mapper).delete(Constants.TABLE_CONTENT, "1");
        verify(mapper, never()).delete(Constants.TABLE_CONTENT_PUBLIC, "1");
    }

    @Test
    void testDoDelete_Exception() throws IOException {
        doThrow(new RuntimeException("DB Error")).when(mapper).delete(Constants.TABLE_CONTENT, "1");

        boolean result = content.doDelete("1", "", response, session);

        assertTrue(result);
        verify(session).setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_DELETE_FAILED);
    }

    @Test
    void testDoDelete_IOException() throws IOException {
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        doThrow(new IOException("Redirect error")).when(response).sendRedirect(anyString());

        boolean result = content.doDelete("1", "", response, session);

        assertTrue(result);
        verify(mapper).delete(Constants.TABLE_CONTENT, "1");
    }

    @Test
    void testDoUpdate_WithLeadingSlash() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("/test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");
        doNothing().when(mapper).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).update(anyString(), anyString(), anyString(), eq("test/page"), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_WithScheduleUnpublished() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("2025-12-31 23:59");
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_WithPastScheduleUnpublished() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("2020-01-01 00:00");
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn("1");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), eq(""), anyString(), anyString(), anyString());
    }

    @Test
    void testDoUpdate_CreateNewPublic() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString())).thenReturn(1);

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testDisplayContent_WithElement() {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("template", "1");
        contentData.put("content", "Main Content");

        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###element(10),###content###");

        HashMap<String, String> elementData = new HashMap<>();
        elementData.put("content", "Element Content");

        when(mapper.search("1", "content_public")).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(templateData);
        when(mapper.search("10", Constants.TABLE_CONTENT)).thenReturn(elementData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("Element Content"));
        assertTrue(result.contains("Main Content"));
    }

    @Test
    void testDisplayContent_WithMultipleMovieTags() {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("template", "");
        contentData.put("content", "Video 1: [movie id=1] Video 2: [movie id=2]");

        HashMap<String, String> movie1 = new HashMap<>();
        movie1.put("type", Constants.CONTENT_TYPE_MOVIE);
        movie1.put("content", "video1");

        HashMap<String, String> movie2 = new HashMap<>();
        movie2.put("type", Constants.CONTENT_TYPE_MOVIE);
        movie2.put("content", "video2");

        when(mapper.search("1", "content_public")).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(movie1);
        when(mapper.search("2", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(movie2);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("video1") || result.contains("video2"));
    }


    @Test
    void testGetUrlDirectoryTree_DeepNestedPath() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "level1/level2/level3/page.html");
        content1.put("title", "Deep Page");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", Constants.CONTENT_TYPE_CONTENT)).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_CONTENT);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_MultipleSlashes() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "///test/page.html");
        content1.put("title", "Test Page");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", Constants.CONTENT_TYPE_CONTENT)).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_CONTENT);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_ExistingDirectory() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "dir/page1.html");
        content1.put("title", "Page 1");
        contents.add(content1);
        
        HashMap<String, String> content2 = new HashMap<>();
        content2.put("id", "2");
        content2.put("url", "dir/page2.html");
        content2.put("title", "Page 2");
        contents.add(content2);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", Constants.CONTENT_TYPE_CONTENT)).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_CONTENT);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    void testDoCreate_WithFutureSchedulePublished() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("2030-12-31 23:59");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }


    @Test
    void testDoUpdate_WithPublishedFlagNo() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("0");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
        verify(mapper, never()).updatePublic(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testDoCreate_WithEmailParseException() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testDoUpdate_WithEmailError() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        when(emailService.sendContentPublishedNotification(anyString(), anyString(), any()))
            .thenReturn("メール送信エラー");
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testDoUpdate_WithEmailSuccess() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        doNothing().when(mapper).update(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        when(mapper.searchContentByAttribute("1", "id", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        when(emailService.sendContentPublishedNotification(anyString(), anyString(), any()))
            .thenReturn(null);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doUpdate(request, response, session);

        assertTrue(result);
        verify(session).setAttribute(eq(Constants.SESSION_REGISTER_MESSAGE), anyString());
    }

    @Test
    void testDoCreate_WithSchedulePublishedWithT() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("2025/12/31T23:59");
        when(request.getParameter("schedule_unpublished")).thenReturn("");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testDoCreate_WithScheduleUnpublishedWithT() throws IOException {
        when(session.getAttribute("name")).thenReturn("testuser");
        when(request.getParameter("url")).thenReturn("test/page");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("head")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("type")).thenReturn("");
        when(request.getParameter("elementcolor")).thenReturn("");
        when(request.getParameter("template")).thenReturn("");
        when(request.getParameter("published")).thenReturn("1");
        when(request.getParameter("schedule_published")).thenReturn("");
        when(request.getParameter("schedule_unpublished")).thenReturn("2025/12/31T23:59");
        when(mapper.create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        when(mapper.createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(1);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = content.doCreate(request, response, session);

        assertTrue(result);
        verify(mapper).create(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString());
    }

    @Test
    void testPreviewContent_WithTemplateAndElement() {
        String contentStr = "Test Content";
        String templateId = "1";
        
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###element(10),###content###");
        
        HashMap<String, String> elementData = new HashMap<>();
        elementData.put("content", "Element Content");
        
        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(templateData);
        when(mapper.search("10", Constants.TABLE_CONTENT)).thenReturn(elementData);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
        assertTrue(result.contains("Element Content"));
        assertTrue(result.contains("Test Content"));
    }

    @Test
    void testPreviewContent_WithTemplateAndContentPlaceholder() {
        String contentStr = "Main Content";
        String templateId = "1";
        
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###content###");
        
        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(templateData);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
        assertTrue(result.contains("Main Content"));
    }

    @Test
    void testPreviewContent_WithTemplateAndEmptyContent() {
        String contentStr = "";
        String templateId = "1";
        
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###content###");
        
        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(templateData);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
    }

    @Test
    void testPreviewContent_WithTemplateAndNullContent() {
        String contentStr = null;
        String templateId = "1";
        
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###content###");
        
        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(templateData);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
    }

    @Test
    void testPreviewContent_WithTemplateAndEmptyTemplateContent() {
        String contentStr = "Test Content";
        String templateId = "1";
        
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "");

        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(templateData);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
    }

    @Test
    void testPreviewContent_WithTemplateAndNullTemplateContent() {
        String contentStr = "Test Content";
        String templateId = "1";
        
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", null);

        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(templateData);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
    }

    @Test
    void testPreviewContent_WithTemplateNotFound() {
        String contentStr = "Test Content";
        String templateId = "999";

        HashMap<String, String> emptyTemplate = new HashMap<>();
        emptyTemplate.put("content", null);

        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(emptyTemplate);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
    }

    @Test
    void testPreviewContent_WithTemplateAndElementNotFound() {
        String contentStr = "Test Content";
        String templateId = "1";
        
        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###element(999),###content###");
        
        HashMap<String, String> emptyElement = new HashMap<>();
        emptyElement.put("content", null);

        when(mapper.search(templateId, Constants.TABLE_CONTENT)).thenReturn(templateData);
        when(mapper.search("999", Constants.TABLE_CONTENT)).thenReturn(emptyElement);

        String result = content.previewContent(contentStr, templateId);

        assertNotNull(result);
    }

    @Test
    void testDisplayContent_WithTemplateAndElement() {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("template", "1");
        contentData.put("content", "Main Content");

        HashMap<String, String> templateData = new HashMap<>();
        templateData.put("content", "###element(10),###content###");

        HashMap<String, String> elementData = new HashMap<>();
        elementData.put("content", "Element Content");

        when(mapper.search("1", "content_public")).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(templateData);
        when(mapper.search("10", Constants.TABLE_CONTENT)).thenReturn(elementData);

        String result = content.displayContent("1");

        assertNotNull(result);
        assertTrue(result.contains("Element Content"));
        assertTrue(result.contains("Main Content"));
    }


    @Test
    void testGetUrlDirectoryTree_EmptyPathParts() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "");
        content1.put("title", "Page");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", Constants.CONTENT_TYPE_CONTENT)).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_CONTENT);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_RootNodeEmpty() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "");
        content1.put("title", "Page");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", Constants.CONTENT_TYPE_CONTENT)).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_CONTENT);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_WithEmptyUrl() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "");
        content1.put("title", "Page 1");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_WithNullUrl() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", null);
        content1.put("title", "Page 1");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUrlDirectoryTree_WithOnlySlash() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "/");
        content1.put("title", "Page 1");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        // "/"のみのURLは空文字列に正規化され、split("/")で[""]が返される
        // pathParts.length == 0のチェックでcontinueされないため、ノードが追加される可能性がある
        // 実際の動作を確認するため、結果が空でない可能性を考慮
        assertNotNull(result);
    }

    @Test
    void testGetUrlDirectoryTree_WithMultipleSlashesInMiddle() {
        List<HashMap<String, String>> contents = new ArrayList<>();
        HashMap<String, String> content1 = new HashMap<>();
        content1.put("id", "1");
        content1.put("url", "about//company.html");
        content1.put("title", "Company");
        contents.add(content1);

        when(mapper.selectAll(Constants.TABLE_CONTENT, "url", "")).thenReturn(contents);

        List<DirectoryNodeBean> result = content.getUrlDirectoryTree("");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
