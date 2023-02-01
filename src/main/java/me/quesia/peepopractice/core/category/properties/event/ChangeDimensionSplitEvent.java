package me.quesia.peepopractice.core.category.properties.event;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ChangeDimensionSplitEvent extends SplitEvent {
    private RegistryKey<World> dimension;

    public RegistryKey<World> getDimension() {
        return this.dimension;
    }

    public boolean hasDimension() {
        return this.dimension != null;
    }

    public ChangeDimensionSplitEvent setDimension(RegistryKey<World> dimension) {
        this.dimension = dimension;
        return this;
    }
}
