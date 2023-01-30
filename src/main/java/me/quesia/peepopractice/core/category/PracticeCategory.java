package me.quesia.peepopractice.core.category;

import me.quesia.peepopractice.core.category.properties.PlayerProperties;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import me.quesia.peepopractice.core.category.properties.WorldProperties;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class PracticeCategory {
    private String id;
    private PlayerProperties playerProperties;
    private final List<StructureProperties> structureProperties = new ArrayList<>();
    private WorldProperties worldProperties;
    private final List<CategorySetting> settings;
    private boolean hidden;

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
        return this;
    }

    public List<StructureProperties> getStructureProperties() {
        return this.structureProperties;
    }

    public boolean hasStructureProperties() {
        return !this.structureProperties.isEmpty();
    }

    public PracticeCategory addStructureProperties(StructureProperties structureProperties) {
        this.structureProperties.add(structureProperties);
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
}
