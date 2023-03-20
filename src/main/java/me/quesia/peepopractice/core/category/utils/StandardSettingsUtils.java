package me.quesia.peepopractice.core.category.utils;

import com.google.gson.JsonObject;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.mixin.access.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;

public class StandardSettingsUtils {
    public static void triggerStandardSettings(PracticeCategory category) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (!config.has(category.getId())) { return; }
        JsonObject settings = config.get(category.getId()).getAsJsonObject();
        if (settings.has("enabled") && !settings.get("enabled").getAsBoolean()) { return; }
        settings.entrySet().forEach(entry -> {
            String k = entry.getKey();
            switch (k) {
                case "fov":
                    MinecraftClient.getInstance().options.fov = entry.getValue().getAsDouble();
                    break;
                case "render_distance":
                    MinecraftClient.getInstance().options.viewDistance = (int) entry.getValue().getAsDouble();
                    break;
                case "entity_distance":
                    MinecraftClient.getInstance().options.entityDistanceScaling = entry.getValue().getAsFloat();
                    break;
                case "sprinting":
                    MinecraftClient.getInstance().options.keySprint.setPressed(true);
                    break;
                case "chunk_borders":
                    MinecraftClient.getInstance().debugRenderer.showChunkBorder = entry.getValue().getAsBoolean();
                    break;
                case "hitboxes":
                    MinecraftClient.getInstance().getEntityRenderManager().setRenderHitboxes(entry.getValue().getAsBoolean());
                    break;
                case "piechart":
                    String value = entry.getValue().getAsString();
                    if (value.split("\\.")[0].equals("root")) {
                        ((MinecraftClientAccessor) MinecraftClient.getInstance()).peepoPractice$setOpenProfilerSection(value.replace('.', '\u001e'));
                    }
                    break;
            }
        });
        MinecraftClient.getInstance().options.write();
    }

    public static boolean getSettingForCategory(PracticeCategory category, String setting, boolean def) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (config.has(category.getId())) {
            JsonObject object = config.get(category.getId()).getAsJsonObject();
            if (object.has(setting)) {
                return object.get(setting).getAsBoolean();
            } else {
                object.addProperty(setting, def);
                writer.put(category.getId(), object);
            }
        } else {
            JsonObject object = new JsonObject();
            object.addProperty(setting, def);
            writer.put(category.getId(), object);
        }
        return def;
    }

    public static double getSettingForCategory(PracticeCategory category, String setting, double def) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (config.has(category.getId())) {
            JsonObject object = config.get(category.getId()).getAsJsonObject();
            if (object.has(setting)) {
                return object.get(setting).getAsDouble();
            } else {
                object.addProperty(setting, def);
                writer.put(category.getId(), object);
            }
        } else {
            JsonObject object = new JsonObject();
            object.addProperty(setting, def);
            writer.put(category.getId(), object);
        }
        return def;
    }

    public static String getSettingForCategory(PracticeCategory category, String setting, String def) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (config.has(category.getId())) {
            JsonObject object = config.get(category.getId()).getAsJsonObject();
            if (object.has(setting)) {
                return object.get(setting).getAsString();
            } else {
                object.addProperty(setting, def);
                writer.put(category.getId(), object);
            }
        } else {
            JsonObject object = new JsonObject();
            object.addProperty(setting, def);
            writer.put(category.getId(), object);
        }
        return def;
    }

    public static void setSettingForCategory(PracticeCategory category, String setting, boolean value) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (!config.has(category.getId())) {
            JsonObject object = new JsonObject();
            object.addProperty(setting, value);
            writer.put(category.getId(), object);
            return;
        }
        JsonObject object = config.get(category.getId()).getAsJsonObject();
        object.addProperty(setting, value);
        writer.put(category.getId(), object);
    }

    public static void setSettingForCategory(PracticeCategory category, String setting, double value) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (!config.has(category.getId())) {
            JsonObject object = new JsonObject();
            object.addProperty(setting, value);
            writer.put(category.getId(), object);
            return;
        }
        JsonObject object = config.get(category.getId()).getAsJsonObject();
        object.addProperty(setting, value);
        writer.put(category.getId(), object);
    }

    public static void setSettingForCategory(PracticeCategory category, String setting, String value) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (!config.has(category.getId())) {
            JsonObject object = new JsonObject();
            object.addProperty(setting, value);
            writer.put(category.getId(), object);
            return;
        }
        JsonObject object = config.get(category.getId()).getAsJsonObject();
        object.addProperty(setting, value);
        writer.put(category.getId(), object);
    }
}
