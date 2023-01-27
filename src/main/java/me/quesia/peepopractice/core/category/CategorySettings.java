package me.quesia.peepopractice.core.category;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;

import java.util.ArrayList;
import java.util.List;

public class CategorySettings {
    private final String id;
    private final String label;
    private final String description;
    private final List<String> choices;
    private String defaultChoice;

    CategorySettings(String id, String label, String description, List<String> options, String defaultOption) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.choices = options;
        this.defaultChoice = defaultOption;

        if (this.choices.contains(this.defaultChoice)) {
            this.defaultChoice = this.choices.get(0);
        }
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getChoices() {
        return this.choices;
    }

    public String getDefaultChoice() {
        return this.defaultChoice;
    }

    public static class Builder {
        private String id;
        private String label;
        private String description;
        private final List<String> choices = new ArrayList<>();
        private String defaultChoice;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setChoices(String[] choices) {
            this.choices.clear();
            this.choices.addAll(List.of(choices));
            return this;
        }

        public Builder addChoice(String choice) {
            this.choices.add(choice);
            return this;
        }

        public Builder setDefaultChoice(String defaultChoice) {
            this.defaultChoice = defaultChoice;
            return this;
        }

        public CategorySettings build() {
            return new CategorySettings(this.id, this.label, this.description, this.choices, this.defaultChoice);
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
        return PracticeCategoryUtils.parseBoolean(getValue(id));
    }

    public static String getValue(String id) {
        return getValue(id, PeepoPractice.CATEGORY.getSettings());
    }

    public static String getValue(String id, List<CategorySettings> settings) {
        PracticeWriter writer = PracticeWriter.CONFIG_WRITER;
        JsonObject config = writer.get();

        JsonElement element = config.get(id);
        CategorySettings categorySettings = getSettingById(id, settings);

        if (categorySettings != null) {
            if (element == null || !categorySettings.getChoices().contains(element.getAsString())) {
                writer.put(id, categorySettings.getDefaultChoice());
                return categorySettings.getDefaultChoice();
            }
        }

        return element.getAsString();
    }

    public static void setValue(String id, String value) {
        PracticeWriter.CONFIG_WRITER.put(id, value);
    }
}
