package verdictum.model;

public class ModelConfig {
    private String displayName;
    private String apiModel;
    private String platform;       // openai, openrouter, anthropic, ollama 等
    private String baseUrl;        // API 端点地址，可为空（使用默认）
    private String responseFormat; // openai, anthropic, ollama

    // Getter & Setter
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getApiModel() { return apiModel; }
    public void setApiModel(String apiModel) { this.apiModel = apiModel; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getResponseFormat() { return responseFormat; }
    public void setResponseFormat(String responseFormat) { this.responseFormat = responseFormat; }
}