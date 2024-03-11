package me.falu.peepopractice.core.category;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import me.falu.peepopractice.core.writer.PracticeWriter;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryPreference {
    private String id;
    private List<String> choices = new ArrayList<>();
    private String defaultChoice;
    private Identifier icon;

    public static int getIndex(String value, List<String> choices) {
        int index = 0;

        for (String choice : choices) {
            if (Objects.equals(value, choice)) {
                break;
            }
            index++;
        }

        return index;
    }

    public static CategoryPreference getPreferenceById(PracticeCategory category, String id) {
        for (CategoryPreference preference : category.getPreferences()) {
            if (preference.id.equals(id)) {
                return preference;
            }
        }
        return null;
    }

    public static boolean getBoolValue(String id) {
        return getBoolValue(PeepoPractice.CATEGORY, id);
    }

    public static boolean getBoolValue(PracticeCategory category, String id) {
        String value = getValue(category, id);
        if (value != null) {
            return PracticeCategoryUtils.parseBoolean(value);
        }
        return false;
    }

    public static String getValue(String id) {
        return getValue(PeepoPractice.CATEGORY, id);
    }

    public static String getOrDefault(PracticeCategory category, String id, String def) {
        String value = getValue(category, id, def);
        if (value == null) {
            setValue(category, id, def);
            return def;
        }
        return value;
    }

    public static String getValue(PracticeCategory category, String id) {
        return getValue(category, id, null);
    }

    public static String getValue(PracticeCategory category, String id, @Nullable String def) {
        PracticeWriter writer = PracticeWriter.PREFERENCES_WRITER;
        JsonObject config = writer.get();

        JsonObject categoryObject = new JsonObject();
        if (config.has(category.getId())) {
            categoryObject = config.get(category.getId()).getAsJsonObject();
        }

        CategoryPreference categoryPreference = getPreferenceById(category, id);

        if (categoryPreference != null) {
            if (!categoryObject.has(id) || !categoryPreference.getChoices().contains(categoryObject.get(id).getAsString())) {
                return categoryPreference.getDefaultChoice();
            }
        }

        try {
            String value = categoryObject.get(id).getAsString();
            // This is for backwards compatibility. It's really scuffed but whatever.
            if (def != null && !value.contains(".")) {
                return def;
            }
            return value;
        } catch (NullPointerException ignored) {
            return def;
        }
    }

    public static String getValue(PracticeCategory category, CategoryPreference categoryPreference) {
        PracticeWriter writer = PracticeWriter.PREFERENCES_WRITER;
        JsonObject config = writer.get();

        JsonObject categoryObject = new JsonObject();
        if (config.has(category.getId())) {
            categoryObject = config.get(category.getId()).getAsJsonObject();
        }

        String id = categoryPreference.getId();
        try {
            if (!categoryObject.has(id) || !categoryPreference.getChoices().contains(categoryObject.get(id).getAsString())) {
                setValue(category, id, categoryPreference.getDefaultChoice());
                return categoryPreference.getDefaultChoice();
            }
        } catch (NullPointerException ignored) {
        }

        try {
            return categoryObject.get(id).getAsString();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public static void setValue(PracticeCategory category, String id, String value) {
        JsonObject config = PracticeWriter.PREFERENCES_WRITER.get();
        JsonObject categoryPreference = new JsonObject();
        if (config.has(category.getId())) {
            categoryPreference = config.get(category.getId()).getAsJsonObject();
        }
        categoryPreference.addProperty(id, value);
        PracticeWriter.PREFERENCES_WRITER.put(category.getId(), categoryPreference);
    }

    public String getId() {
        return this.id;
    }

    public CategoryPreference setId(String id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return new TranslatableText("peepopractice.preferences." + this.id).getString();
    }

    public String getDescription() {
        return new TranslatableText("peepopractice.preferences." + this.id + ".info").getString();
    }

    public List<String> getChoices() {
        return this.choices;
    }

    public CategoryPreference setChoices(List<String> choices) {
        this.choices = choices;
        return this;
    }

    @SuppressWarnings("unused")
    public CategoryPreference setChoices(String[] choices) {
        this.choices.clear();
        this.choices.addAll(Lists.newArrayList(choices));
        return this;
    }

    public String getDefaultChoice() {
        return this.defaultChoice;
    }

    public CategoryPreference setDefaultChoice(String defaultChoice) {
        this.defaultChoice = defaultChoice;
        return this;
    }

    public Identifier getIcon() {
        return this.icon;
    }

    public CategoryPreference setIcon(Identifier icon) {
        this.icon = icon;
        return this;
    }
}
