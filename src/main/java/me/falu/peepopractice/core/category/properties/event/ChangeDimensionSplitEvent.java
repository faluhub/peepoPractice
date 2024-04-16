package me.falu.peepopractice.core.category.properties.event;

import lombok.Getter;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@Getter
public class ChangeDimensionSplitEvent extends SplitEvent {
    private RegistryKey<World> toDimension;
    private RegistryKey<World> fromDimension;

    public ChangeDimensionSplitEvent setToDimension(RegistryKey<World> toDimension) {
        this.toDimension = toDimension;
        return this;
    }

    public boolean hasToDimension() {
        return this.toDimension != null;
    }

    public ChangeDimensionSplitEvent setFromDimension(RegistryKey<World> fromDimension) {
        this.fromDimension = fromDimension;
        return this;
    }

    public boolean hasFromDimension() {
        return this.fromDimension != null;
    }
}
