package me.quesia.peepopractice.core;

import com.google.gson.*;
import me.quesia.peepopractice.PeepoPractice;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PracticeWriter {
    public static final PracticeWriter CONFIG_WRITER = new PracticeWriter("config.json");
    public static final PracticeWriter INVENTORY_WRITER = new PracticeWriter("inventory.json");
    public static final PracticeWriter PB_WRITER = new PracticeWriter("personal_bests.json");

    private final File file;

    public PracticeWriter(String fileName) {
        this.file = this.create(fileName);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File create(String fileName) {
        try {
            File folder = FabricLoader.getInstance().getConfigDir().resolve(PeepoPractice.MOD_NAME).toFile();
            if (!folder.exists()) { folder.mkdirs(); }
            File file = folder.toPath().resolve(fileName).toFile();
            if (!file.exists()) { file.createNewFile(); }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void write(JsonObject config) {
        this.create(this.file.getName());
        Util.getServerWorkerExecutor().execute(() -> {
            try {
                FileWriter writer = new FileWriter(this.file);

                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
                writer.flush();
                writer.close();
            } catch (IOException ignored) {}
        });
    }

    public JsonObject get() {
        this.create(this.file.getName());

        try {
            FileReader reader = new FileReader(this.file);
            JsonParser parser = new JsonParser();

            Object obj = parser.parse(reader);

            return obj == null || obj.equals(JsonNull.INSTANCE) ? new JsonObject() : (JsonObject) obj;
        } catch (IOException ignored) {}

        return null;
    }

    public void put(String element, long value) {
        JsonObject config = this.get();

        if (config != null) {
            if (config.has(element)) {
                config.remove(element);
            }
            config.addProperty(element, value);

            this.write(config);
        }
    }

    public void put(String element, JsonObject obj) {
        JsonObject config = this.get();

        if (config != null) {
            if (config.has(element)) {
                config.remove(element);
            }
            config.add(element, obj);

            this.write(config);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void put(String element, JsonArray array) {
        JsonObject config = this.get();

        if (config != null) {
            if (config.has(element)) {
                config.remove(element);
            }
            config.add(element, array);

            this.write(config);
        }
    }
}
