package me.quesia.peepopractice.core.category.properties;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@SuppressWarnings("UnusedDeclaration")
public class WorldProperties {
    private RegistryKey<World> worldRegistryKey;

    public RegistryKey<World> getWorldRegistryKey() {
        return this.worldRegistryKey;
    }

    public boolean hasWorldRegistryKey() {
        return this.worldRegistryKey != null;
    }

    public WorldProperties setWorldRegistryKey(RegistryKey<World> worldRegistryKey) {
        this.worldRegistryKey = worldRegistryKey;
        return this;
    }
}
