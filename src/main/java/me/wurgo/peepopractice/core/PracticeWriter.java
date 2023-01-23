package me.wurgo.peepopractice.core;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.wurgo.peepopractice.PeepoPractice;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class PracticeWriter {
    public static PracticeWriter CONFIG_WRITER = new PracticeWriter("config");
    public static PracticeWriter INVENTORY_WRITER = new PracticeWriter("inventory");

    private final File file;

    public PracticeWriter(String fileName) {
        this.file = this.create(fileName);
    }

    private File create(String fileName) {
        File folder = new File(FabricLoader.getInstance().getConfigDir() + "/peepoPractice");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                PeepoPractice.log("Created config folder.");
            }
        }

        File file = new File(FabricLoader.getInstance().getConfigDir() + "/peepoPractice/" + fileName + ".json");

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    PeepoPractice.log("Created file '" + fileName + "'.");
                }
            } catch (IOException ignored) {}
        }

        return file;
    }

    private void write(JsonObject config) {
        this.create(this.file.getName());
        try {
            FileWriter writer = new FileWriter(this.file);

            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
            writer.flush();
            writer.close();

            PeepoPractice.log("Saved file '" + this.file.getName() + "'. (Put)");
        } catch (IOException ignored) {}
    }

    public JsonObject get() {
        this.create(this.file.getName());
        try {
            FileReader reader = new FileReader(this.file);
            JsonParser parser = new JsonParser();

            Object obj = parser.parse(reader);

            return Objects.equals(JsonNull.INSTANCE, obj) ? new JsonObject() : (JsonObject) obj;
        } catch (IOException ignored) {}

        return null;
    }

    public void put(String element, String value) {
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
}
