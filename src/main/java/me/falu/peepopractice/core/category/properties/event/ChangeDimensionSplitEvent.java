package me.falu.peepopractice.core.category.properties.event;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ChangeDimensionSplitEvent extends SplitEvent {
    private RegistryKey<World> toDimension;
    private RegistryKey<World> fromDimension;

    public RegistryKey<World> getToDimension() {
        return this.toDimension;
    }

    public ChangeDimensionSplitEvent setToDimension(RegistryKey<World> toDimension) {
        this.toDimension = toDimension;
        return this;
    }

    public boolean hasToDimension() {
        return this.toDimension != null;
    }

    public RegistryKey<World> getFromDimension() {
        return this.fromDimension;
    }

    public ChangeDimensionSplitEvent setFromDimension(RegistryKey<World> fromDimension) {
        this.fromDimension = fromDimension;
        return this;
    }

    public boolean hasFromDimension() {
        return this.fromDimension != null;
    }
}
