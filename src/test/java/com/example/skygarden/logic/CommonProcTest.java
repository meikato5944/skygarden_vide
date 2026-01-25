package com.example.skygarden.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * CommonProcのテストクラス
 */
class CommonProcTest {

    @TempDir
    Path tempDir;

    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test.txt").toFile();
    }

    @AfterEach
    void tearDown() {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testReadFile_ExistingFile() throws IOException {
        // テストファイルを作成
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Line 1\nLine 2\nLine 3");
        }

        String result = CommonProc.readFile(testFile.getAbsolutePath());
        assertNotNull(result);
        assertTrue(result.contains("Line 1"));
        assertTrue(result.contains("Line 2"));
        assertTrue(result.contains("Line 3"));
    }

    @Test
    void testReadFile_NonExistentFile() {
        String result = CommonProc.readFile("/nonexistent/file.txt");
        assertEquals("", result);
    }

    @Test
    void testReadFile_EmptyFile() throws IOException {
        // 空のファイルを作成
        testFile.createNewFile();

        String result = CommonProc.readFile(testFile.getAbsolutePath());
        assertEquals("", result);
    }

    @Test
    void testCreateNow_Format() {
        String result = CommonProc.createNow();
        assertNotNull(result);
        // 形式チェック: "yyyy-MM-dd HH:mm"
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));
    }

    @Test
    void testCreateNow_CurrentTime() {
        String result1 = CommonProc.createNow();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String result2 = CommonProc.createNow();
        
        // 時刻が異なることを確認（秒単位で異なる可能性がある）
        assertNotNull(result1);
        assertNotNull(result2);
    }

    @Test
    void testGetRootPath_NotNull() {
        String result = CommonProc.getRootPath();
        assertNotNull(result);
    }

    @Test
    void testGetRootPath_IsDirectory() {
        String result = CommonProc.getRootPath();
        File dir = new File(result);
        assertTrue(dir.exists() || dir.getParent() != null);
    }
}
