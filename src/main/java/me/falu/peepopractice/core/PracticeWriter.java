package me.falu.peepopractice.core;

import com.google.gson.*;
import me.falu.peepopractice.PeepoPractice;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PracticeWriter {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final PracticeWriter PREFERENCES_WRITER = new PracticeWriter("preferences.json");
    public static final PracticeWriter INVENTORY_WRITER = new PracticeWriter("inventory.json");
    public static final PracticeWriter COMPLETIONS_WRITER = new PracticeWriter("completions.json");
    public static final PracticeWriter STANDARD_SETTINGS_WRITER = new PracticeWriter("standard_settings.json");
    public static final PracticeWriter GLOBAL_CONFIG = new PracticeWriter("global_config.json");
    private final File file;
    private JsonObject local;

    public PracticeWriter(String fileName) {
        this.file = this.create(fileName);
        this.update();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File create(String fileName) {
        try {
            File folder = FabricLoader.getInstance().getConfigDir().resolve(PeepoPractice.MOD_NAME).toFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = folder.toPath().resolve(fileName).toFile();
            if (!file.exists()) {
                file.createNewFile();
                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("writer/" + fileName);
                if (stream != null) {
                    FileWriter writer = new FileWriter(file);
                    writer.write(GSON.toJson(new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))));
                    writer.flush();
                    writer.close();
                }
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write() {
        this.create(this.file.getName());
        try {
            FileWriter writer = new FileWriter(this.file);

            writer.write(GSON.toJson(this.local));
            writer.flush();
            writer.close();

            PeepoPractice.log("Wrote to '" + this.file.getName() + "'.");
        } catch (IOException ignored) {}
        this.update();
    }

    public void update() {
        this.local = null;
        this.local = this.get();
    }

    public JsonObject get() {
        if (this.local != null) { return this.local; }

        this.create(this.file.getName());

        try {
            FileReader reader = new FileReader(this.file);
            JsonParser parser = new JsonParser();

            Object obj = parser.parse(reader);
            reader.close();

            return obj == null || obj.equals(JsonNull.INSTANCE) ? new JsonObject() : (JsonObject) obj;
        } catch (IOException ignored) {}

        return null;
    }

    public void put(String element, JsonElement obj) {
        if (this.local == null) {
            this.update();
        }
        if (this.local.has(element)) {
            this.local.remove(element);
        }
        this.local.add(element, obj);
    }
}
