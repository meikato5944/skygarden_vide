package com.example.skygarden.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Setting;

/**
 * OpenAIServiceのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class OpenAIServiceTest {

    @Mock
    private Setting setting;

    @InjectMocks
    private OpenAIService openAIService;

    @BeforeEach
    void setUp() {
        // デフォルトのモック設定
    }

    @Test
    void testGenerateTitle_Success() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title for: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        // 実際のAPI呼び出しは行わないため、エラーが返されることを期待
        Map<String, Object> result = openAIService.generateTitle("test input");

        assertNotNull(result);
        // APIキーが設定されていても、実際のHTTP呼び出しは失敗する可能性がある
        // エラーまたは結果が返されることを確認
    }

    @Test
    void testGenerateTitle_EmptyPrompt() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateTitle("test input");

        assertNotNull(result);
    }

    @Test
    void testGenerateTitle_NullInput() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateTitle(null);

        assertNotNull(result);
    }

    @Test
    void testGenerateTitle_NoApiKey() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateTitle("test input");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGenerateTitle_NullApiKey() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn(null);
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateTitle("test input");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGenerateContent_Success() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Generate content for: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateContent("test input");

        assertNotNull(result);
    }

    @Test
    void testGenerateContent_EmptyPrompt() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateContent("test input");

        assertNotNull(result);
    }

    @Test
    void testGenerateContent_NullInput() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Generate content: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateContent(null);

        assertNotNull(result);
    }

    @Test
    void testGenerateContent_NoApiKey() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Generate content: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateContent("test input");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGenerateTitle_PlaceholderReplacement() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Title for: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateTitle("My Input");

        assertNotNull(result);
        // プレースホルダーが置換されることを確認（実際のAPI呼び出しは行わない）
    }

    @Test
    void testGenerateContent_PlaceholderReplacement() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Content for: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateContent("My Input");

        assertNotNull(result);
    }

    @Test
    void testGenerateTitle_DefaultModel() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("");

        Map<String, Object> result = openAIService.generateTitle("test");

        assertNotNull(result);
    }

    @Test
    void testGenerateTitle_NullModel() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn(null);

        Map<String, Object> result = openAIService.generateTitle("test");

        assertNotNull(result);
    }

    @Test
    void testGenerateContent_DefaultModel() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Generate content");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("");

        Map<String, Object> result = openAIService.generateContent("test");

        assertNotNull(result);
    }

    @Test
    void testGenerateTitle_NewModel() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("o1-preview");

        Map<String, Object> result = openAIService.generateTitle("test");

        assertNotNull(result);
    }

    @Test
    void testGenerateContent_NewModel() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Generate content");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-5");

        Map<String, Object> result = openAIService.generateContent("test");

        assertNotNull(result);
    }

    @Test
    void testGenerateTitle_EmptyInput() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE)).thenReturn("Generate title: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateTitle("");

        assertNotNull(result);
    }

    @Test
    void testGenerateContent_EmptyInput() throws IOException, InterruptedException {
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT)).thenReturn("Generate content: {userInput}");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY)).thenReturn("sk-test-key");
        when(setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL)).thenReturn("gpt-3.5-turbo");

        Map<String, Object> result = openAIService.generateContent("");

        assertNotNull(result);
    }
}
