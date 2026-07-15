package verdictum.loader;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleLoader {
    private Map<String, Map<String, List<String>>> tasksConfig;
    private Map<String, String> modules;

    public RuleLoader() throws Exception {
        // 加载 config.yaml（先读字符串再解析，防止流被提前关闭）
        String configPath = "/modules/config.yaml";
        String configContent;
        try (InputStream is = getClass().getResourceAsStream(configPath)) {
            if (is == null) throw new RuntimeException("找不到 " + configPath);
            configContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        if (configContent.trim().isEmpty()) throw new RuntimeException(configPath + " 是空文件");

        Yaml yaml = new Yaml();
        Map<String, Object> rawConfig = yaml.load(configContent);
        this.tasksConfig = (Map<String, Map<String, List<String>>>) rawConfig.get("tasks");

        // 加载其他模块文件
        String[] moduleNames = {"base_values", "action_coefficients", "interference_factors"};
        Map<String, String> mods = new HashMap<>();
        for (String name : moduleNames) {
            String path = "/modules/" + name + ".txt";
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is == null) throw new RuntimeException("找不到 " + path);
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                mods.put(name, content);
            }
        }
        this.modules = mods;
    }

    public String getPrompt(String taskName, String userInput) {
        Map<String, List<String>> task = tasksConfig.get(taskName);
        if (task == null) throw new IllegalArgumentException("未知任务: " + taskName);
        List<String> moduleNames = task.get("modules");
        StringBuilder sb = new StringBuilder();
        for (String name : moduleNames) {
            String moduleContent = modules.get(name);
            if (moduleContent != null) {
                sb.append(moduleContent).append("\n");
            }
        }
        sb.append("\n---\n用户输入：\n").append(userInput);
        return sb.toString();
    }
}