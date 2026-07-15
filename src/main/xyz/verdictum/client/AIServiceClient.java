package verdictum.client;

import com.google.gson.*;
import okhttp3.*;
import verdictum.config.ApiKeyManager;
import verdictum.model.ModelConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AIServiceClient {

    private static final String DEFAULT_OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String DEFAULT_ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String DEFAULT_OLLAMA_URL = "http://localhost:11434/api/generate";

    private final OkHttpClient client;
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final Gson gson = new Gson();
    private ModelConfig currentModelConfig;

    public AIServiceClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 设置当前模型配置（从 AnalysisPanel 传入选择项）
     */
    public void setModelConfig(ModelConfig config) {
        this.currentModelConfig = config;
    }

    /**
     * 统一调用入口，根据平台自动构造请求和解析响应
     */
    public String call(String prompt) throws Exception {
        if (currentModelConfig == null) {
            throw new IllegalStateException("未设置模型配置，请先选择一个 AI 模型。");
        }

        String platform = currentModelConfig.getPlatform();
        if (platform == null) platform = "openai"; // 向后兼容

        String apiKey = ApiKeyManager.loadKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("未配置 API Key，请在设置中输入。");
        }

        // 根据平台构造请求和解析
        switch (platform.toLowerCase()) {
            case "openai":
            case "openrouter":
                return callOpenAiStyle(currentModelConfig, prompt, apiKey);
            case "anthropic":
                return callAnthropic(currentModelConfig, prompt, apiKey);
            case "ollama":
                return callOllama(currentModelConfig, prompt);
            default:
                // 未知平台尝试按 OpenAI 格式处理
                return callOpenAiStyle(currentModelConfig, prompt, apiKey);
        }
    }

    // ------------------- OpenAI / OpenRouter 格式 -------------------
    private String callOpenAiStyle(ModelConfig config, String prompt, String apiKey) throws Exception {
        String url = config.getBaseUrl();
        if (url == null || url.isEmpty()) {
            url = "openrouter".equals(config.getPlatform()) ? DEFAULT_OPENROUTER_URL : DEFAULT_OPENAI_URL;
        }

        JsonObject body = new JsonObject();
        body.addProperty("model", config.getApiModel());
        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", prompt);
        messages.add(userMsg);
        body.add("messages", messages);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(body), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String respBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("请求失败 (" + response.code() + "): " + respBody);
            }
            return extractOpenAiContent(respBody);
        }
    }

    private String extractOpenAiContent(String responseBody) {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
                if (message != null) {
                    return message.get("content").getAsString();
                }
            }
        } catch (Exception ignored) { }
        return responseBody; // 解析失败返回原始文本
    }

    // ------------------- Anthropic 格式 -------------------
    private String callAnthropic(ModelConfig config, String prompt, String apiKey) throws Exception {
        String url = config.getBaseUrl();
        if (url == null || url.isEmpty()) {
            url = DEFAULT_ANTHROPIC_URL;
        }

        JsonObject body = new JsonObject();
        body.addProperty("model", config.getApiModel());
        body.addProperty("max_tokens", 4096);
        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", prompt);
        messages.add(userMsg);
        body.add("messages", messages);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(body), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String respBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("请求失败 (" + response.code() + "): " + respBody);
            }
            return extractAnthropicContent(respBody);
        }
    }

    private String extractAnthropicContent(String responseBody) {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray content = root.getAsJsonArray("content");
            if (content != null && !content.isEmpty()) {
                JsonObject first = content.get(0).getAsJsonObject();
                if (first.has("text")) {
                    return first.get("text").getAsString();
                }
            }
        } catch (Exception ignored) { }
        return responseBody;
    }

    // ------------------- Ollama 格式 -------------------
    private String callOllama(ModelConfig config, String prompt) throws Exception {
        String url = config.getBaseUrl();
        if (url == null || url.isEmpty()) {
            url = DEFAULT_OLLAMA_URL;
        }

        JsonObject body = new JsonObject();
        body.addProperty("model", config.getApiModel());
        body.addProperty("prompt", prompt);
        body.addProperty("stream", false);   // 不使用流式

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(gson.toJson(body), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String respBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("请求失败 (" + response.code() + "): " + respBody);
            }
            return extractOllamaContent(respBody);
        }
    }

    private String extractOllamaContent(String responseBody) {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            // Ollama 非流式返回 {"response":"..."}
            if (root.has("response")) {
                return root.get("response").getAsString();
            }
            // 也可能返回 message.content 类似结构，做一次兼容
            if (root.has("message")) {
                return root.getAsJsonObject("message").get("content").getAsString();
            }
        } catch (Exception ignored) { }
        return responseBody;
    }
}