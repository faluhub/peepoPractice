package me.quesia.peepopractice.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.quesia.peepopractice.PeepoPractice;

import java.util.Arrays;
import java.util.List;

public class CategorySettings {
    public final String id;
    public final String label;
    public final String description;
    public final List<String> options;
    public String defaultOption;

    public CategorySettings(String id, String label, String description, String[] options, String defaultOption) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.options = Arrays.asList(options);
        this.defaultOption = defaultOption;

        if (this.options.contains(this.defaultOption)) {
            this.defaultOption = this.options.get(0);
        }
    }

    public static CategorySettings getSettingById(String id, List<CategorySettings> settings) {
        for (CategorySettings setting : settings) {
            if (setting.id.equals(id)) {
                return setting;
            }
        }
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static boolean getBoolValue(String id) {
        return CategoryUtils.parseBoolean(getValue(id));
    }

    public static String getValue(String id) {
        return getValue(id, PeepoPractice.CATEGORY.settings);
    }

    public static String getValue(String id, List<CategorySettings> settings) {
        PracticeWriter writer = PracticeWriter.CONFIG_WRITER;
        JsonObject config = writer.get();

        JsonElement element = config.get(id);
        CategorySettings categorySettings = getSettingById(id, settings);

        if (categorySettings != null) {
            if (element == null || !categorySettings.options.contains(element.getAsString())) {
                writer.put(id, categorySettings.defaultOption);
                return categorySettings.defaultOption;
            }
        }

        return element.getAsString();
    }

    public static void setValue(String id, String value) {
        PracticeWriter.CONFIG_WRITER.put(id, value);
    }
}
