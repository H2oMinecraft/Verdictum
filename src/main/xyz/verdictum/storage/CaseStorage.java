package verdictum.storage;

import com.google.gson.*;
import verdictum.model.CaseRecord;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class CaseStorage {
    private static final Path STORAGE_DIR = Paths.get(System.getProperty("user.dir"), "cases");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    static {
        try { Files.createDirectories(STORAGE_DIR); } catch (IOException ignored) {}
    }

    public static void save(CaseRecord record) {
        record.id = UUID.randomUUID().toString().substring(0, 8);
        record.createdAt = LocalDateTime.now();
        try {
            String json = GSON.toJson(record);
            Files.writeString(STORAGE_DIR.resolve(record.id + ".json"), json);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<CaseRecord> loadAll() {
        List<CaseRecord> list = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(STORAGE_DIR, "*.json")) {
            for (Path p : ds) {
                String content = Files.readString(p);
                CaseRecord record = GSON.fromJson(content, CaseRecord.class);
                list.add(record);
            }
        } catch (IOException ignored) {}
        list.sort((a, b) -> b.createdAt.compareTo(a.createdAt));
        return list;
    }

    public static void delete(String id) {
        try { Files.deleteIfExists(STORAGE_DIR.resolve(id + ".json")); } catch (IOException ignored) {}
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString());
        }
    }
}