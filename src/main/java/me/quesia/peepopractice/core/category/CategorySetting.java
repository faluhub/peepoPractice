package me.quesia.peepopractice.core.category;

import com.google.gson.JsonObject;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;

import java.util.ArrayList;
import java.util.List;

public class CategorySetting {
    private String id;
    private String label;
    private String description;
    private List<String> choices = new ArrayList<>();
    private String defaultChoice;

    public String getId() {
        return this.id;
    }

    public CategorySetting setId(String id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public CategorySetting setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public CategorySetting setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getChoices() {
        return this.choices;
    }

    public CategorySetting setChoices(List<String> choices) {
        this.choices = choices;
        return this;
    }

    public CategorySetting setChoices(String[] choices) {
        this.choices.clear();
        this.choices.addAll(List.of(choices));
        return this;
    }

    public CategorySetting addChoice(String choice) {
        this.choices.add(choice);
        return this;
    }

    public String getDefaultChoice() {
        return this.defaultChoice;
    }

    public CategorySetting setDefaultChoice(String defaultChoice) {
        if (this.choices.contains(this.defaultChoice)) { this.defaultChoice = defaultChoice; }
        else if (this.choices.size() > 0) { this.defaultChoice = this.choices.get(0); }
        return this;
    }

    public static CategorySetting getSettingById(PracticeCategory category, String id) {
        for (CategorySetting setting : category.getSettings()) {
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
        return getValue(PeepoPractice.CATEGORY, id);
    }

    public static String getValue(PracticeCategory category, String id) {
        PracticeWriter writer = PracticeWriter.CONFIG_WRITER;
        JsonObject config = writer.get();

        JsonObject categoryObject = new JsonObject();
        if (config.has(category.getId())) {
            categoryObject = config.get(category.getId()).getAsJsonObject();
        }

        CategorySetting categorySettings = getSettingById(category, id);

        if (categorySettings != null) {
            if (!categoryObject.has(id) || !categorySettings.getChoices().contains(categoryObject.get(id).getAsString())) {
                setValue(category, id, categorySettings.getDefaultChoice());
                return categorySettings.getDefaultChoice();
            }
        }

        return categoryObject.get(id).getAsString();
    }

    public static void setValue(PracticeCategory category, String id, String value) {
        JsonObject config = PracticeWriter.CONFIG_WRITER.get();
        JsonObject categorySettings = new JsonObject();
        if (config.has(category.getId())) {
            categorySettings = config.get(category.getId()).getAsJsonObject();
        }

        categorySettings.addProperty(id, value);
        PracticeWriter.CONFIG_WRITER.put(category.getId(), categorySettings);
    }
}
