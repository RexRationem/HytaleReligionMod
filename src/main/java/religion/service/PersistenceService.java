package religion.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import religion.ReligionMod;
import religion.data.Religion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PersistenceService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path saveDir;

    public PersistenceService(ReligionMod plugin, EventLogService eventLog) {
        this.saveDir = Path.of("plugins", "ReligionMod", "data");
    }

    public void saveAll(Collection<Religion> religions) {
        try {
            Files.createDirectories(saveDir);
            for (Religion religion : religions) {
                Path path = saveDir.resolve(safeName(religion.name) + ".json");
                Files.writeString(path, gson.toJson(religion), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            System.err.println("[ReligionMod] Failed saving religion data");
            e.printStackTrace();
        }
    }

    public List<Religion> loadAll() {
        List<Religion> religions = new ArrayList<>();
        try {
            Files.createDirectories(saveDir);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(saveDir, "*.json")) {
                for (Path path : stream) {
                    String raw = Files.readString(path, StandardCharsets.UTF_8);
                    Religion religion = gson.fromJson(raw, Religion.class);
                    if (religion != null) {
                        religions.add(religion);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ReligionMod] Failed loading religion data");
            e.printStackTrace();
        }
        return religions;
    }

    private String safeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}