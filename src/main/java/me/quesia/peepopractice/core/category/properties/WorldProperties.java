package me.quesia.peepopractice.core.category.properties;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class WorldProperties {
    private RegistryKey<World> worldRegistryKey;

    public RegistryKey<World> getWorldRegistryKey() {
        return this.worldRegistryKey;
    }

    public WorldProperties setWorldRegistryKey(RegistryKey<World> worldRegistryKey) {
        this.worldRegistryKey = worldRegistryKey;
        return this;
    }
}
