package me.falu.peepopractice.core.category.utils;

import com.google.gson.JsonObject;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.writer.PracticeWriter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

@SuppressWarnings("DuplicatedCode")
public class StandardSettingsUtils {
    public static final DoubleOption FOV = new DoubleOption("options.fov", 30.0D, 110.0D, 1.0F, gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "fov", 70.0D), (gameOptions, aDouble) -> StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "fov", aDouble), (gameOptions, doubleOption) -> {
        double d = doubleOption.get(gameOptions);
        MutableText mutableText = doubleOption.getDisplayPrefix();
        if (d == 70.0D) {
            return mutableText.append(new TranslatableText("options.fov.min"));
        } else if (d == doubleOption.getMax()) {
            return mutableText.append(new TranslatableText("options.fov.max"));
        }
        return mutableText.append(Integer.toString((int) d));
    });
    public static final DoubleOption RENDER_DISTANCE = new DoubleOption("options.renderDistance", 2.0D, 16.0D, 1.0F, gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "render_distance", 16.0D), (gameOptions, aDouble) -> StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "render_distance", aDouble), (gameOptions, doubleOption) -> {
        double d = doubleOption.get(gameOptions);
        return doubleOption.getDisplayPrefix().append(new TranslatableText("options.chunks", (int) d));
    });
    public static final DoubleOption ENTITY_DISTANCE_SCALING = new DoubleOption("options.entityDistanceScaling", 0.5D, 5.0D, 0.25F, gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "entity_distance", 5.0D), (gameOptions, aDouble) -> StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "entity_distance", aDouble), (gameOptions, doubleOption) -> {
        double d = doubleOption.get(gameOptions);
        return doubleOption.getDisplayPrefix().append(new TranslatableText("options.entityDistancePercent", (int) (d * 100.0)));
    });
    public static final BooleanOption CHUNK_BORDERS = new BooleanOption(getKey("chunk_borders"), gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "chunk_borders", false), (gameOptions, aBoolean) -> StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "chunk_borders", aBoolean));
    public static final BooleanOption HITBOXES = new BooleanOption(getKey("hitboxes"), gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "hitboxes", false), (gameOptions, aBoolean) -> StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "hitboxes", aBoolean));
    public static final CyclingOption PERSPECTIVE = new CyclingOption(getKey("perspective"), (gameOptions, integer) -> {
        int value = StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "perspective", 0) + 1;
        if (value > 2) {
            value = 0;
        }
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "perspective", value);
    }, (gameOptions, cyclingOption) -> {
        int value = StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "perspective", 0);
        MutableText text = cyclingOption.getDisplayPrefix();
        if (value == 0) {
            return text.append(new TranslatableText("peepopractice.perspective.first"));
        } else if (value == 1) {
            return text.append(new TranslatableText("peepopractice.perspective.back"));
        } else if (value == 2) {
            return text.append(new TranslatableText("peepopractice.perspective.front"));
        }
        return text;
    });

    private static String getKey(String key) {
        return "peepopractice.standard_setting." + key;
    }

    public static void triggerStandardSettings(PracticeCategory category) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (!config.has(category.getId())) {
            return;
        }
        JsonObject settings = config.get(category.getId()).getAsJsonObject();
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
                case "chunk_borders":
                    MinecraftClient.getInstance().debugRenderer.showChunkBorder = entry.getValue().getAsBoolean();
                    break;
                case "hitboxes":
                    MinecraftClient.getInstance().getEntityRenderManager().setRenderHitboxes(entry.getValue().getAsBoolean());
                    break;
                case "piechart":
                    String value = entry.getValue().getAsString();
                    if (value.split("\\.")[0].equals("root")) {
                        MinecraftClient.getInstance().openProfilerSection = value.replace('.', '\u001e');
                    }
                    break;
                case "perspective":
                    MinecraftClient.getInstance().options.perspective = entry.getValue().getAsInt();
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

    public static int getSettingForCategory(PracticeCategory category, String setting, int def) {
        PracticeWriter writer = PracticeWriter.STANDARD_SETTINGS_WRITER;
        JsonObject config = writer.get();
        if (config.has(category.getId())) {
            JsonObject object = config.get(category.getId()).getAsJsonObject();
            if (object.has(setting)) {
                return object.get(setting).getAsInt();
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
