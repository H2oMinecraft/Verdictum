package verdictum.config;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

public class ApiKeyManager {
    private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".verdictum");
    private static final Path KEY_FILE = CONFIG_DIR.resolve("config.dat");

    public static void saveKey(String apiKey) throws IOException {
        Files.createDirectories(CONFIG_DIR);
        String encoded = Base64.getEncoder().encodeToString(apiKey.getBytes());
        Files.writeString(KEY_FILE, encoded);
    }

    public static String loadKey() throws IOException {
        if (!Files.exists(KEY_FILE)) return null;
        String encoded = Files.readString(KEY_FILE).trim();
        return new String(Base64.getDecoder().decode(encoded));
    }

    public static boolean exists() {
        return Files.exists(KEY_FILE);
    }

    public static void deleteKey() throws IOException {
        Files.deleteIfExists(KEY_FILE);
    }
}