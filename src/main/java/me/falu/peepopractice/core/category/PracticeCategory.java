package me.falu.peepopractice.core.category;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.category.properties.event.SplitEvent;
import me.falu.peepopractice.core.exception.NotInitializedException;
import me.falu.peepopractice.core.writer.PracticeWriter;
import me.falu.peepopractice.gui.screen.InventoryOptionsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class PracticeCategory {
    @Getter private final List<StructureProperties> structureProperties = new ArrayList<>();
    @Getter private final List<CategoryPreference> preferences;
    private final boolean aa;
    private final Map<String, Object> customValues = new HashMap<>();
    private final Map<String, Object> permaValues = new HashMap<>();
    @Getter private String id;
    @Getter private PlayerProperties playerProperties;
    @Getter private WorldProperties worldProperties;
    @Getter private SplitEvent splitEvent;
    @Getter private boolean hidden;
    private boolean canHaveEmptyInventory;
    @Getter private boolean fillerCategory;

    public PracticeCategory() {
        this(false);
    }

    public PracticeCategory(boolean aa) {
        this.preferences = new ArrayList<>();
        this.aa = aa;
    }

    public PracticeCategory setId(String id) {
        this.id = id;
        return this;
    }

    public PracticeCategory setPlayerProperties(PlayerProperties playerProperties) {
        this.playerProperties = playerProperties;
        this.playerProperties.setCategory(this);
        return this;
    }

    public boolean hasPlayerProperties() {
        return this.playerProperties != null;
    }

    public StructureProperties findStructureProperties(StructureFeature<?> feature) {
        for (StructureProperties properties : this.structureProperties) {
            if (properties.isSameStructure(feature)) {
                return properties;
            }
        }
        return null;
    }

    public StructureProperties findStructureProperties(ConfiguredStructureFeature<?, ?> feature) {
        for (StructureProperties properties : this.structureProperties) {
            if (properties.isSameStructure(feature)) {
                return properties;
            }
        }
        return null;
    }

    public boolean hasStructureProperties() {
        return !this.structureProperties.isEmpty();
    }

    public PracticeCategory addStructureProperties(StructureProperties structureProperties) {
        this.structureProperties.add((StructureProperties) structureProperties.setCategory(this));
        return this;
    }

    public PracticeCategory setWorldProperties(WorldProperties worldProperties) {
        this.worldProperties = worldProperties;
        this.worldProperties.setCategory(this);
        return this;
    }

    public boolean hasWorldProperties() {
        return this.worldProperties != null;
    }

    public PracticeCategory setSplitEvent(SplitEvent splitEvent) {
        this.splitEvent = splitEvent;
        this.splitEvent.setCategory(this);
        return this;
    }

    public boolean hasSplitEvent() {
        return this.splitEvent != null;
    }

    public PracticeCategory setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public boolean hasPreferences() {
        return !this.preferences.isEmpty();
    }

    public PracticeCategory addPreference(CategoryPreference preference) {
        this.preferences.add(preference);
        return this;
    }

    public boolean getCanHaveEmptyInventory() {
        return this.canHaveEmptyInventory;
    }

    public PracticeCategory setCanHaveEmptyInventory(boolean canHaveEmptyInventory) {
        this.canHaveEmptyInventory = canHaveEmptyInventory;
        return this;
    }

    public PracticeCategory setFillerCategory(boolean fillerCategory) {
        this.fillerCategory = fillerCategory;
        return this;
    }

    public PracticeCategory register() {
        if (this.aa) {
            PracticeCategoriesAA.ALL.add(this);
        } else {
            PracticeCategoriesAny.ALL.add(this);
        }
        return this;
    }

    public Object getCustomValue(String key) {
        return this.customValues.get(key);
    }

    public Object getPermaValue(String key) {
        return this.permaValues.get(key);
    }

    public boolean hasCustomValue(String key) {
        return this.customValues.containsKey(key);
    }

    public boolean hasPermaValue(String key) {
        return this.permaValues.containsKey(key);
    }

    public void putCustomValue(String key, Object value) {
        this.customValues.put(key, value);
    }

    public PracticeCategory putPermaValue(String key, Object value) {
        this.permaValues.put(key, value);
        return this;
    }

    public void removeCustomValue(String key) {
        if (this.hasCustomValue(key)) {
            this.customValues.remove(key);
        }
    }

    public String getTranslatedName() {
        return I18n.translate("peepopractice.categories." + this.id);
    }

    public String getName(boolean showPb) {
        StringBuilder text = new StringBuilder((this.isFillerCategory() ? Formatting.ITALIC : "") + this.getTranslatedName() + (showPb || this.isFillerCategory() ? Formatting.RESET : ""));
        if (showPb) {
            text.append(this.getPbText());
        }
        return text.toString();
    }

    public String getPbText() {
        if (this.hasSplitEvent()) {
            PracticeTypes.CompareType compareType = PracticeTypes.CompareType.fromLabel(CategoryPreference.getOrDefault(this, "compare_type", PracticeTypes.CompareType.PB.getLabel()));
            if (compareType != null) {
                boolean comparePb = compareType.equals(PracticeTypes.CompareType.PB);
                boolean hasTime = comparePb ? this.getSplitEvent().hasPb() : this.getSplitEvent().hasCompletedTimes();
                if (hasTime) {
                    String timeString = comparePb ? this.getSplitEvent().getPbString() : this.getSplitEvent().getAverageString();
                    return (comparePb ? Formatting.GREEN : Formatting.AQUA) + " (" + timeString + ")";
                } else {
                    return Formatting.GRAY + " " + new TranslatableText("peepopractice.text.no_pb_or_avg", I18n.translate(compareType.getLabel())).getString();
                }
            }
        }
        return "";
    }

    public boolean isAA() {
        return this.aa;
    }

    public boolean hasConfiguredInventory() {
        if (this.canHaveEmptyInventory) {
            return true;
        }
        PracticeWriter writer = PracticeWriter.INVENTORY_WRITER;
        JsonObject config = writer.get();
        if (!config.has(this.getId())) {
            return false;
        }
        String value = CategoryPreference.getValue(this, InventoryOptionsScreen.SELECTED_INVENTORY);
        int selected = value != null ? Integer.parseInt(value) : 0;
        JsonArray profiles = config.getAsJsonArray(this.getId());
        if (profiles.size() <= selected) {
            return false;
        }
        return profiles.get(selected).getAsJsonObject().size() > 0;
    }

    public void reset() {
        this.customValues.clear();
    }

    public interface ExecuteReturnTask<T> {
        @Nullable T execute(PracticeCategory category, Random random, ServerWorld world) throws NotInitializedException;
    }
}
