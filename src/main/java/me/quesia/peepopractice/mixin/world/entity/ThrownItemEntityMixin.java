package me.quesia.peepopractice.mixin.world.entity;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.event.ThrowEntitySplitEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownItemEntity.class)
public abstract class ThrownItemEntityMixin extends ThrownEntity {
    protected ThrownItemEntityMixin(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "setItem", at = @At("HEAD"))
    private void peepoPractice$onItemThrown(ItemStack item, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof ThrowEntitySplitEvent) {
                ThrowEntitySplitEvent event = (ThrowEntitySplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasItem() && event.getItem().equals(item.getItem())) {
                    event.complete(this.getOwner() != null && this.getOwner().isAlive());
                    this.remove();
                }
            }
        }
    }
}
