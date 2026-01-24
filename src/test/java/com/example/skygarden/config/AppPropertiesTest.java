package com.example.skygarden.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * AppPropertiesのテストクラス
 */
class AppPropertiesTest {

    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
    }

    @Test
    void testDefaultPagination() {
        assertNotNull(appProperties.getPagination());
        assertEquals(20, appProperties.getPagination().getPageSize());
    }

    @Test
    void testSetPagination() {
        AppProperties.Pagination pagination = new AppProperties.Pagination();
        pagination.setPageSize(50);
        appProperties.setPagination(pagination);

        assertEquals(50, appProperties.getPagination().getPageSize());
    }

    @Test
    void testDefaultFile() {
        assertNotNull(appProperties.getFile());
        assertEquals("preview.html", appProperties.getFile().getPreviewFileName());
        assertEquals("uploads/images", appProperties.getFile().getUploadDir());
        assertEquals("uploads/files", appProperties.getFile().getFileUploadDir());
    }

    @Test
    void testSetFile() {
        AppProperties.File file = new AppProperties.File();
        file.setPreviewFileName("custom-preview.html");
        file.setUploadDir("custom/images");
        file.setFileUploadDir("custom/files");
        appProperties.setFile(file);

        assertEquals("custom-preview.html", appProperties.getFile().getPreviewFileName());
        assertEquals("custom/images", appProperties.getFile().getUploadDir());
        assertEquals("custom/files", appProperties.getFile().getFileUploadDir());
    }

    @Test
    void testPagination_SetPageSize() {
        AppProperties.Pagination pagination = new AppProperties.Pagination();
        pagination.setPageSize(30);
        assertEquals(30, pagination.getPageSize());
    }

    @Test
    void testPagination_ZeroPageSize() {
        AppProperties.Pagination pagination = new AppProperties.Pagination();
        pagination.setPageSize(0);
        assertEquals(0, pagination.getPageSize());
    }

    @Test
    void testPagination_NegativePageSize() {
        AppProperties.Pagination pagination = new AppProperties.Pagination();
        pagination.setPageSize(-1);
        assertEquals(-1, pagination.getPageSize());
    }

    @Test
    void testFile_SetPreviewFileName() {
        AppProperties.File file = new AppProperties.File();
        file.setPreviewFileName("test.html");
        assertEquals("test.html", file.getPreviewFileName());
    }

    @Test
    void testFile_SetUploadDir() {
        AppProperties.File file = new AppProperties.File();
        file.setUploadDir("/path/to/images");
        assertEquals("/path/to/images", file.getUploadDir());
    }

    @Test
    void testFile_SetFileUploadDir() {
        AppProperties.File file = new AppProperties.File();
        file.setFileUploadDir("/path/to/files");
        assertEquals("/path/to/files", file.getFileUploadDir());
    }

    @Test
    void testFile_NullValues() {
        AppProperties.File file = new AppProperties.File();
        file.setPreviewFileName(null);
        file.setUploadDir(null);
        file.setFileUploadDir(null);

        assertNull(file.getPreviewFileName());
        assertNull(file.getUploadDir());
        assertNull(file.getFileUploadDir());
    }

    @Test
    void testFile_EmptyValues() {
        AppProperties.File file = new AppProperties.File();
        file.setPreviewFileName("");
        file.setUploadDir("");
        file.setFileUploadDir("");

        assertEquals("", file.getPreviewFileName());
        assertEquals("", file.getUploadDir());
        assertEquals("", file.getFileUploadDir());
    }
}
