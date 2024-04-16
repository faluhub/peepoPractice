package me.falu.peepopractice.core.category.preferences;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.writer.PracticeWriter;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Getter
public class CategoryPreference<T extends Enum<?>> {
    private String id;
    private T defaultValue;
    private Identifier icon;

    public boolean getBoolValue() {
        return this.getBoolValue(PeepoPractice.CATEGORY);
    }

    public boolean getBoolValue(PracticeCategory category) {
        T value = this.getValue(category);
        return value.name().equals("ENABLED");
    }

    public T getValue() {
        return this.getValue(PeepoPractice.CATEGORY);
    }

    @SuppressWarnings("unchecked")
    public T getValue(PracticeCategory category) {
        JsonObject config = PracticeWriter.PREFERENCES_WRITER.get();
        JsonObject categoryObj = config.has(category.getId()) ? config.getAsJsonObject(category.getId()) : new JsonObject();
        if (categoryObj.has(this.id)) {
            String value = categoryObj.get(this.id).getAsString();
            for (Enum<?> v : this.defaultValue.getDeclaringClass().getEnumConstants()) {
                if (v.name().equals(value)) {
                    return (T) v;
                }
            }
        }
        this.setValue(category, this.defaultValue);
        return this.defaultValue;
    }

    @SuppressWarnings("unchecked")
    public T getNextValue(PracticeCategory category) {
        T value = this.getValue(category);
        int index = value.ordinal();
        if (index + 1 == value.getDeclaringClass().getEnumConstants().length) {
            index = 0;
        } else {
            index++;
        }
        return (T) value.getDeclaringClass().getEnumConstants()[index];
    }

    public void setValue(PracticeCategory category, T value) {
        JsonObject config = PracticeWriter.PREFERENCES_WRITER.get();
        JsonObject categoryObj = config.has(category.getId()) ? config.getAsJsonObject(category.getId()) : new JsonObject();
        categoryObj.addProperty(this.id, value.name());
        PracticeWriter.PREFERENCES_WRITER.put(category.getId(), categoryObj);
    }

    public void advanceValue(PracticeCategory category) {
        this.setValue(category, this.getNextValue(category));
    }

    public CategoryPreference<T> setId(String id) {
        this.id = id;
        return this;
    }

    public Text getValueLabel(PracticeCategory category) {
        return this.getValueLabel(category, false);
    }

    public Text getValueLabel(PracticeCategory category, boolean styled) {
        T value = this.getValue(category);
        String style;
        switch (value.name()) {
            case "RANDOM":
                style = styled ? "" + Formatting.YELLOW : "";
                return new LiteralText(style).append(new TranslatableText("peepopractice.text.random"));
            case "ENABLED":
                style = styled ? "" + Formatting.GREEN : "";
                return new LiteralText(style).append(new TranslatableText("peepopractice.text.enabled"));
            case "DISABLED":
                style = styled ? "" + Formatting.RED : "";
                return new LiteralText(style).append(new TranslatableText("peepopractice.text.disabled"));
            default:
                String identifier = "";
                if (value instanceof PreferenceTypes.ConfigTypeBase) {
                    identifier = ((PreferenceTypes.ConfigTypeBase) value).getIdentifier() + ".";
                }
                return new TranslatableText("peepopractice.types." + identifier + value.name().toLowerCase());
        }
    }

    public Text getLabel() {
        return new TranslatableText("peepopractice.preferences." + this.id);
    }

    public Text getDescription() {
        return new TranslatableText("peepopractice.preferences." + this.id + ".info");
    }

    public CategoryPreference<T> setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public CategoryPreference<T> setIcon(Identifier icon) {
        this.icon = icon;
        return this;
    }
}
