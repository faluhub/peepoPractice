package me.quesia.peepopractice.core.category.properties.event;

import net.minecraft.entity.EntityType;

public class EnterVehicleSplitEvent extends SplitEvent {
    private EntityType<?> vehicle;

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
}
