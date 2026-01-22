package com.example.skygarden.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Setting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * OpenAI API呼び出しサービス
 * 
 * このサービスクラスはOpenAI APIを使用してタイトルや本文を生成する機能を提供します。
 * 設定画面で設定されたAPIキー、モデル、プロンプトを使用してOpenAI APIを呼び出します。
 * 
 * 主な機能:
 * - タイトル生成（generateTitle）
 * - 本文生成（generateContent）
 * 
 * エラーハンドリング:
 * - APIキー未設定時のエラー
 * - API呼び出しエラー（タイムアウト、HTTPエラーなど）
 * - レスポンス解析エラー
 * 
 * @see Setting 設定管理のビジネスロジック
 */
@Service
public class OpenAIService {
    
	/** 設定管理のビジネスロジック */
	@Autowired
	private Setting setting;
	
	/** OpenAI APIのエンドポイントURL */
	private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
	
	/** タイムアウト時間（秒） */
	private static final int TIMEOUT_SECONDS = 60;
	
	/** JSON解析用のObjectMapper */
	private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * タイトルを生成する
     * 設定画面で設定されたプロンプトを使用してOpenAI APIを呼び出し、タイトルを生成します。
     * 
     * @param userInput ユーザーが入力した情報（プロンプトの{userInput}に置き換えられる）
     * @return 生成結果を含むMap（"title"キーに生成されたタイトル、または"error"キーにエラー情報）
     * @throws IOException IO例外
     * @throws InterruptedException 割り込み例外
     */
    public Map<String, Object> generateTitle(String userInput) throws IOException, InterruptedException {
        String prompt = setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_TITLE);
        if (prompt.isEmpty()) {
            prompt = "以下の情報を基に、適切なWebページのタイトルを1つ提案してください。\n\n情報: {userInput}\n\nタイトルは簡潔で、検索エンジン最適化を考慮したものにしてください。";
        }
        prompt = prompt.replace("{userInput}", userInput != null ? userInput : "");
        
        return callOpenAI(prompt, "title");
    }
    
    /**
     * 本文を生成する
     * 設定画面で設定されたプロンプトを使用してOpenAI APIを呼び出し、本文を生成します。
     * 
     * @param userInput ユーザーが入力した情報（プロンプトの{userInput}に置き換えられる）
     * @return 生成結果を含むMap（"content"キーに生成された本文、または"error"キーにエラー情報）
     * @throws IOException IO例外
     * @throws InterruptedException 割り込み例外
     */
    public Map<String, Object> generateContent(String userInput) throws IOException, InterruptedException {
        String prompt = setting.getOpenAISetting(Constants.CONFIG_OPENAI_PROMPT_CONTENT);
        if (prompt.isEmpty()) {
            prompt = "以下の情報を基に、Webページの本文（HTML形式）を生成してください。\n\n情報: {userInput}\n\n適切なHTMLタグを使用し、読みやすい形式で作成してください。";
        }
        prompt = prompt.replace("{userInput}", userInput != null ? userInput : "");
        
        return callOpenAI(prompt, "content");
    }
    
    /**
     * OpenAI APIを呼び出す
     * 
     * @param prompt プロンプト
     * @param type 生成タイプ（"title" または "content"）
     * @return 生成結果またはエラー情報を含むMap
     * @throws IOException IO例外
     * @throws InterruptedException 割り込み例外
     */
    private Map<String, Object> callOpenAI(String prompt, String type) throws IOException, InterruptedException {
        // #region agent log
        try {
            Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
            String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"OpenAIService.java:100\",\"message\":\"callOpenAI開始\",\"data\":{\"type\":\"%s\",\"promptLength\":%d},\"timestamp\":%d}\n", type, prompt != null ? prompt.length() : 0, System.currentTimeMillis());
            Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        
        String apiKey = setting.getOpenAISetting(Constants.CONFIG_OPENAI_API_KEY);
        String model = setting.getOpenAISetting(Constants.CONFIG_OPENAI_MODEL);
        
        // APIキーのチェック
        if (apiKey == null || apiKey.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "OpenAI APIキーが設定されていません。設定画面でAPIキーを設定してください。"));
            return error;
        }
        
        // モデルのデフォルト値設定
        if (model == null || model.isEmpty()) {
            model = "gpt-3.5-turbo";
        }
        
        // #region agent log
        try {
            Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
            String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"B\",\"location\":\"OpenAIService.java:115\",\"message\":\"モデルとAPIキー確認\",\"data\":{\"model\":\"%s\",\"apiKeyLength\":%d},\"timestamp\":%d}\n", model, apiKey != null ? apiKey.length() : 0, System.currentTimeMillis());
            Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        
        // リクエストボディの構築
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "あなたはWebコンテンツ作成の専門家です。");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        
        requestBody.put("messages", new Object[]{systemMessage, userMessage});
        
        // モデルによって適切なパラメータを使用
        // o-seriesやGPT-5シリーズなど新しいモデルはmax_completion_tokensを使用
        boolean isNewModel = model != null && (model.startsWith("o") || model.startsWith("gpt-5") || model.startsWith("gpt-4.1"));
        if (isNewModel) {
            requestBody.put("max_completion_tokens", 2000);
        } else {
            // 従来のモデル（GPT-3.5、GPT-4など）はmax_tokensを使用
            requestBody.put("max_tokens", 2000);
        }
        
        // temperatureパラメータは、o-seriesやGPT-5シリーズなど新しいモデルではサポートされていない
        // またはデフォルト値（1）のみがサポートされているため、従来のモデルのみに設定
        if (!isNewModel) {
            requestBody.put("temperature", 0.7);
        }
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        // HTTPクライアントの作成
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();
        
        // HTTPリクエストの作成
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OPENAI_API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();
        
        // API呼び出し
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (java.net.http.HttpTimeoutException e) {
            // #region agent log
            try {
                Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
                String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C\",\"location\":\"OpenAIService.java:165\",\"message\":\"タイムアウトエラー\",\"data\":{\"error\":\"%s\"},\"timestamp\":%d}\n", e.getMessage().replace("\"", "\\\""), System.currentTimeMillis());
                Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception ex) {}
            // #endregion
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "リクエストがタイムアウトしました（60秒）。時間がかかるモデルの場合は、もう一度お試しください。"));
            return error;
        } catch (java.io.IOException e) {
            // #region agent log
            try {
                Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
                String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D\",\"location\":\"OpenAIService.java:171\",\"message\":\"IOエラー\",\"data\":{\"error\":\"%s\"},\"timestamp\":%d}\n", e.getMessage().replace("\"", "\\\""), System.currentTimeMillis());
                Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception ex) {}
            // #endregion
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "ネットワークエラーが発生しました: " + e.getMessage()));
            return error;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "リクエストが中断されました。"));
            return error;
        }
        
        // #region agent log
        try {
            Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
            String responseBody = response.body() != null ? (response.body().length() > 500 ? response.body().substring(0, 500) : response.body()) : "null";
            String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"E\",\"location\":\"OpenAIService.java:182\",\"message\":\"レスポンス受信\",\"data\":{\"statusCode\":%d,\"bodyLength\":%d,\"bodyPreview\":\"%s\"},\"timestamp\":%d}\n", response.statusCode(), response.body() != null ? response.body().length() : 0, responseBody.replace("\"", "\\\"").replace("\n", "\\n"), System.currentTimeMillis());
            Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        
        // エラーレスポンスの処理
        if (response.statusCode() != 200) {
            Map<String, Object> error = new HashMap<>();
            try {
                JsonNode errorNode = objectMapper.readTree(response.body());
                String errorMessage = errorNode.has("error") && errorNode.get("error").has("message") 
                    ? errorNode.get("error").get("message").asText()
                    : "OpenAI API呼び出しエラー: " + response.statusCode();
                error.put("error", Map.of("message", errorMessage));
            } catch (Exception e) {
                error.put("error", Map.of("message", "OpenAI API呼び出しエラー: " + response.statusCode() + " - " + e.getMessage()));
            }
            return error;
        }
        
        // レスポンスの解析
        JsonNode jsonResponse;
        try {
            jsonResponse = objectMapper.readTree(response.body());
        } catch (Exception e) {
            // #region agent log
            try {
                Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
                String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"F\",\"location\":\"OpenAIService.java:197\",\"message\":\"JSON解析エラー\",\"data\":{\"error\":\"%s\",\"body\":\"%s\"},\"timestamp\":%d}\n", e.getMessage().replace("\"", "\\\""), (response.body() != null && response.body().length() > 200 ? response.body().substring(0, 200) : response.body()).replace("\"", "\\\"").replace("\n", "\\n"), System.currentTimeMillis());
                Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception ex) {}
            // #endregion
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "レスポンスのJSON解析に失敗しました: " + e.getMessage()));
            return error;
        }
        
        // #region agent log
        try {
            Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
            boolean hasChoices = jsonResponse.has("choices");
            int choicesSize = hasChoices && jsonResponse.get("choices").isArray() ? jsonResponse.get("choices").size() : 0;
            String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"OpenAIService.java:200\",\"message\":\"JSON解析成功\",\"data\":{\"hasChoices\":%s,\"choicesSize\":%d},\"timestamp\":%d}\n", hasChoices, choicesSize, System.currentTimeMillis());
            Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        
        // choices配列の存在確認
        if (!jsonResponse.has("choices") || !jsonResponse.get("choices").isArray() || jsonResponse.get("choices").size() == 0) {
            // #region agent log
            try {
                Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
                java.util.List<String> fieldNames = new java.util.ArrayList<>();
                jsonResponse.fieldNames().forEachRemaining(fieldNames::add);
                String responseKeys = fieldNames.isEmpty() ? "none" : String.join(",", fieldNames);
                String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H\",\"location\":\"OpenAIService.java:267\",\"message\":\"choices配列が存在しない\",\"data\":{\"hasChoices\":%s,\"responseKeys\":\"%s\"},\"timestamp\":%d}\n", jsonResponse.has("choices"), responseKeys, System.currentTimeMillis());
                Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {}
            // #endregion
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "レスポンスにchoices配列が存在しません。レスポンス形式が正しくありません。"));
            return error;
        }
        
        JsonNode firstChoice = jsonResponse.get("choices").get(0);
        
        // #region agent log
        try {
            Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
            boolean hasMessage = firstChoice.has("message");
            boolean hasContent = hasMessage && firstChoice.get("message").has("content");
            String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"I\",\"location\":\"OpenAIService.java:215\",\"message\":\"choices[0]確認\",\"data\":{\"hasMessage\":%s,\"hasContent\":%s},\"timestamp\":%d}\n", hasMessage, hasContent, System.currentTimeMillis());
            Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        
        // messageとcontentの存在確認
        if (!firstChoice.has("message")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "レスポンスにmessageが存在しません。レスポンス形式が正しくありません。"));
            return error;
        }
        
        if (!firstChoice.get("message").has("content")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", Map.of("message", "レスポンスにcontentが存在しません。レスポンス形式が正しくありません。"));
            return error;
        }
        
        String generatedText = firstChoice.get("message").get("content").asText();
        
        // #region agent log
        try {
            Path logPath = Paths.get("/Users/katomei/Desktop/skygarden_videcode/.cursor/debug.log");
            String finishReason = firstChoice.has("finish_reason") ? firstChoice.get("finish_reason").asText() : "unknown";
            String logEntry = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"J\",\"location\":\"OpenAIService.java:308\",\"message\":\"生成テキスト取得\",\"data\":{\"textLength\":%d,\"finishReason\":\"%s\",\"isEmpty\":%s},\"timestamp\":%d}\n", generatedText != null ? generatedText.length() : 0, finishReason, (generatedText == null || generatedText.isEmpty()), System.currentTimeMillis());
            Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        
        // contentが空の場合の処理
        if (generatedText == null || generatedText.trim().isEmpty()) {
            String finishReason = firstChoice.has("finish_reason") ? firstChoice.get("finish_reason").asText() : "unknown";
            Map<String, Object> error = new HashMap<>();
            if ("length".equals(finishReason)) {
                error.put("error", Map.of("message", "生成されたコンテンツがトークン制限に達しました。max_completion_tokensの値を増やすか、より短いプロンプトを試してください。"));
            } else {
                error.put("error", Map.of("message", "生成されたコンテンツが空です。finish_reason: " + finishReason));
            }
            return error;
        }
        
        // マークダウンのコードブロック記号（```）を前後から削除
        String cleanedText = generatedText.trim();
        // 先頭の```を削除（言語指定がある場合も考慮: ```html, ```xml など）
        cleanedText = cleanedText.replaceAll("^```[a-zA-Z]*\\s*", "");
        // 末尾の```を削除
        cleanedText = cleanedText.replaceAll("\\s*```$", "");
        cleanedText = cleanedText.trim();
        
        // 結果の構築
        Map<String, Object> result = new HashMap<>();
        if ("title".equals(type)) {
            result.put("title", cleanedText);
        } else {
            result.put("content", cleanedText);
        }
        
        return result;
    }
}
