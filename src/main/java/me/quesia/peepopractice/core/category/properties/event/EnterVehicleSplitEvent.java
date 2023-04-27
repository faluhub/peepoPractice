package me.quesia.peepopractice.core.category.properties.event;

import net.minecraft.entity.EntityType;

public class EnterVehicleSplitEvent extends SplitEvent {
    private EntityType<?> vehicle;
    private boolean keepItem;

    public EntityType<?> getVehicle() {
        return this.vehicle;
    }

    public boolean hasVehicle() {
        return this.vehicle != null;
    }

    public EnterVehicleSplitEvent setVehicle(EntityType<?> vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public boolean shouldKeepItem() {
        return this.keepItem;
    }

    public EnterVehicleSplitEvent setKeepItem(boolean keepItem) {
        this.keepItem = keepItem;
        return this;
    }
}
