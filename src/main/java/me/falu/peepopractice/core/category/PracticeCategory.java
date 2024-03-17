package me.falu.peepopractice.core.category;

import com.google.gson.JsonObject;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.category.properties.event.SplitEvent;
import me.falu.peepopractice.core.exception.NotInitializedException;
import me.falu.peepopractice.core.writer.PracticeWriter;
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
    private final List<StructureProperties> structureProperties = new ArrayList<>();
    private final List<CategoryPreference> preferences;
    private final boolean custom;
    private final boolean aa;
    private final Map<String, Object> customValues = new HashMap<>();
    private final Map<String, Object> permaValues = new HashMap<>();
    private String id;
    private PlayerProperties playerProperties;
    private WorldProperties worldProperties;
    private SplitEvent splitEvent;
    private boolean hidden;
    private boolean canHaveEmptyInventory;
    private boolean fillerCategory;

    public PracticeCategory() {
        this(false, false);
    }

    public PracticeCategory(boolean aa) {
        this(aa, false);
    }

    public PracticeCategory(boolean aa, boolean custom) {
        this.preferences = new ArrayList<>();
        this.aa = aa;
        this.custom = custom;
        if (!this.custom) {
            if (aa) {
                PracticeCategoriesAA.ALL.add(this);
            } else {
                PracticeCategoriesAny.ALL.add(this);
            }
        }
    }

    public void register() {
        if (this.custom) {
            PracticeCategoriesAny.ALL.add(this);
        }
    }

    public String getId() {
        return this.id;
    }

    public PracticeCategory setId(String id) {
        this.id = id;
        return this;
    }

    public PlayerProperties getPlayerProperties() {
        return this.playerProperties;
    }

    public PracticeCategory setPlayerProperties(PlayerProperties playerProperties) {
        this.playerProperties = playerProperties;
        this.playerProperties.setCategory(this);
        return this;
    }

    public boolean hasPlayerProperties() {
        return this.playerProperties != null;
    }

    public List<StructureProperties> getStructureProperties() {
        return this.structureProperties;
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

    public WorldProperties getWorldProperties() {
        return this.worldProperties;
    }

    public PracticeCategory setWorldProperties(WorldProperties worldProperties) {
        this.worldProperties = worldProperties;
        this.worldProperties.setCategory(this);
        return this;
    }

    public boolean hasWorldProperties() {
        return this.worldProperties != null;
    }

    public SplitEvent getSplitEvent() {
        return this.splitEvent;
    }

    public PracticeCategory setSplitEvent(SplitEvent splitEvent) {
        this.splitEvent = splitEvent;
        this.splitEvent.setCategory(this);
        return this;
    }

    public boolean hasSplitEvent() {
        return this.splitEvent != null;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public PracticeCategory setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public List<CategoryPreference> getPreferences() {
        return this.preferences;
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

    public boolean isFillerCategory() {
        return this.fillerCategory;
    }

    public PracticeCategory setFillerCategory(boolean fillerCategory) {
        this.fillerCategory = fillerCategory;
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
                boolean hasTime = compareType.equals(PracticeTypes.CompareType.PB) ? this.getSplitEvent().hasPb() : this.getSplitEvent().hasCompletedTimes();
                if (hasTime) {
                    String timeString = compareType.equals(PracticeTypes.CompareType.PB) ? this.getSplitEvent().getPbString() : this.getSplitEvent().getAverageString();
                    return Formatting.GREEN + " (" + timeString + ")";
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
        JsonObject inventory = config.get(this.getId()).getAsJsonObject();
        return inventory.size() > 0;
    }

    public void reset() {
        this.customValues.clear();
    }

    public interface ExecuteReturnTask<T> {
        @Nullable T execute(PracticeCategory category, Random random, ServerWorld world) throws NotInitializedException;
    }
}
