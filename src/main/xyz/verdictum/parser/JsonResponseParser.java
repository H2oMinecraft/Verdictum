package verdictum.parser;

import com.google.gson.*;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.List;

public class JsonResponseParser {
    private static final Gson gson = new Gson();

    public static List<Crime> parseCrimes(String rawResponse) throws Exception {
        String json = extractJson(rawResponse);
        if (json == null || json.isBlank()) {
            throw new Exception("AI 返回内容中未找到有效 JSON。原始响应前200字符: " + rawResponse.substring(0, Math.min(200, rawResponse.length())));
        }
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonElement crimesElement = root.get("crimes");
            if (crimesElement == null || crimesElement.isJsonNull()) {
                throw new Exception("AI 返回的 JSON 中没有 'crimes' 字段或为 null。原始 JSON: " + json);
            }
            Crime[] crimes = gson.fromJson(crimesElement, Crime[].class);
            if (crimes == null) {
                throw new Exception("解析 'crimes' 得到空数组。原始 JSON: " + json);
            }
            return List.of(crimes);
        } catch (JsonSyntaxException e) {
            throw new Exception("JSON 语法错误。提取的 JSON: " + json + "\n原始响应前200字符: " + rawResponse.substring(0, Math.min(200, rawResponse.length())), e);
        }
    }

    public static List<InterferenceFactor> parseInterference(String rawResponse) throws Exception {
        String json = extractJson(rawResponse);
        if (json == null || json.isBlank()) {
            throw new Exception("AI 返回内容中未找到有效 JSON。原始响应前200字符: " + rawResponse.substring(0, Math.min(200, rawResponse.length())));
        }
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonElement factorsElement = root.get("interference_factors");
            if (factorsElement == null || factorsElement.isJsonNull()) {
                throw new Exception("AI 返回的 JSON 中没有 'interference_factors' 字段或为 null。原始 JSON: " + json);
            }
            InterferenceFactor[] factors = gson.fromJson(factorsElement, InterferenceFactor[].class);
            if (factors == null) {
                throw new Exception("解析 'interference_factors' 得到空数组。原始 JSON: " + json);
            }
            return List.of(factors);
        } catch (JsonSyntaxException e) {
            throw new Exception("JSON 语法错误。提取的 JSON: " + json + "\n原始响应前200字符: " + rawResponse.substring(0, Math.min(200, rawResponse.length())), e);
        }
    }

    private static String extractJson(String raw) {
        if (raw == null) return null;
        String cleaned = raw.trim();
        // 去除 Markdown 代码块
        if (cleaned.startsWith("```")) {
            int firstNewline = cleaned.indexOf('\n');
            if (firstNewline != -1) cleaned = cleaned.substring(firstNewline + 1);
            int endMarker = cleaned.lastIndexOf("```");
            if (endMarker != -1) cleaned = cleaned.substring(0, endMarker);
            cleaned = cleaned.trim();
        }
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) return cleaned.substring(start, end + 1);
        return null;
    }
}