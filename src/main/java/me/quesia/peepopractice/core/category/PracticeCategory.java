package me.quesia.peepopractice.core.category;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PracticeCategory {
    private String id;
    private RegistryKey<World> worldRegistryKey;
    private final List<CategorySetting> settings;

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

    public RegistryKey<World> getWorldRegistryKey() {
        return this.worldRegistryKey;
    }

    public PracticeCategory setWorldRegistryKey(RegistryKey<World> worldRegistryKey) {
        this.worldRegistryKey = worldRegistryKey;
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
