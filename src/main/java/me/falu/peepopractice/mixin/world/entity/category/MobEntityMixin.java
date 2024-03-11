package me.falu.peepopractice.mixin.world.entity.category;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(method = "dropLoot", at = @At("HEAD"), cancellable = true)
    protected void peepoPractice$onDropLoot(CallbackInfo ci) { }
}
