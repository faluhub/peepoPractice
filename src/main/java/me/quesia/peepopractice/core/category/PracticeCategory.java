package me.quesia.peepopractice.core.category;

import me.quesia.peepopractice.core.category.properties.PlayerProperties;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import me.quesia.peepopractice.core.category.properties.WorldProperties;
import me.quesia.peepopractice.core.category.properties.event.SplitEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class PracticeCategory {
    private String id;
    private PlayerProperties playerProperties;
    private final List<StructureProperties> structureProperties = new ArrayList<>();
    private WorldProperties worldProperties;
    private SplitEvent splitEvent;
    private final List<CategorySetting> settings;
    private boolean hidden;
    private final Map<String, Object> customValues = new HashMap<>();

    public PracticeCategory() {
        this.settings = new ArrayList<>();

        PracticeCategories.ALL.add(this);
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

    public boolean hasPlayerProperties() {
        return this.playerProperties != null;
    }

    public PracticeCategory setPlayerProperties(PlayerProperties playerProperties) {
        this.playerProperties = playerProperties;
        this.playerProperties.setCategory(this);
        return this;
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

    public boolean hasWorldProperties() {
        return this.worldProperties != null;
    }

    public PracticeCategory setWorldProperties(WorldProperties worldProperties) {
        this.worldProperties = worldProperties;
        this.worldProperties.setCategory(this);
        return this;
    }

    public SplitEvent getSplitEvent() {
        return this.splitEvent;
    }

    public boolean hasSplitEvent() {
        return this.splitEvent != null;
    }

    public PracticeCategory setSplitEvent(SplitEvent splitEvent) {
        this.splitEvent = splitEvent;
        this.splitEvent.setCategory(this);
        return this;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public PracticeCategory setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public List<CategorySetting> getSettings() {
        return this.settings;
    }

    public PracticeCategory addSetting(CategorySetting setting) {
        this.settings.add(setting);
        return this;
    }

    public Object getCustomValue(String key) {
        return this.customValues.get(key);
    }

    public boolean hasCustomValue(String key) {
        return this.customValues.containsKey(key);
    }

    public void putCustomValue(String key, Object value) {
        this.customValues.put(key, value);
    }

    public void removeCustomValue(String key) {
        if (this.hasCustomValue(key)) {
            this.customValues.remove(key);
        }
    }

    public String getName(boolean showPb) {
        StringBuilder text = new StringBuilder();
        boolean shouldCapitalise = true;
        for (Character c : this.getId().toCharArray()) {
            if (shouldCapitalise) {
                text.append(c.toString().toUpperCase(Locale.ROOT));
                shouldCapitalise = false;
            } else if (c.equals('_')) {
                text.append(" ");
                shouldCapitalise = true;
            } else {
                text.append(c.toString().toLowerCase(Locale.ROOT));
            }
        }
        if (showPb && this.hasSplitEvent()) {
            if (this.getSplitEvent().hasPb()) {
                text.append(Formatting.GOLD).append(" (").append(this.getSplitEvent().getPbString()).append(")");
            } else {
                text.append(Formatting.GRAY).append(" (No PB)");
            }
        }
        return text.toString();
    }

    public void reset() {
        this.customValues.clear();
    }

    public interface ExecuteReturnTask<T> {
        @Nullable T execute(PracticeCategory category, Random random, ServerWorld world);
    }
}
