package me.quesia.peepopractice.mixin.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Inject(method = "method_29276", at = @At("RETURN"), cancellable = true)
    private static void danceOnPlayerKill(LivingEntity livingEntity, LivingEntity livingEntity2, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity2.getType().equals(EntityType.PLAYER)) {
            cir.setReturnValue(true);
        }
    }
}
