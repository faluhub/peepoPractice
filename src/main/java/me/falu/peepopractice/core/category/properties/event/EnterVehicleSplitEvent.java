package me.falu.peepopractice.core.category.properties.event;

import lombok.Getter;
import net.minecraft.entity.EntityType;

public class EnterVehicleSplitEvent extends SplitEvent {
    @Getter private EntityType<?> vehicle;
    private boolean keepItem;

    public EnterVehicleSplitEvent setVehicle(EntityType<?> vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public boolean hasVehicle() {
        return this.vehicle != null;
    }

    public boolean shouldKeepItem() {
        return this.keepItem;
    }

    public EnterVehicleSplitEvent setKeepItem(boolean keepItem) {
        this.keepItem = keepItem;
        return this;
    }
}
