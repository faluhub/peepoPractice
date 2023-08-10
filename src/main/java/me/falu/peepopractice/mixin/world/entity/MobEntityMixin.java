package me.falu.peepopractice.mixin.world.entity;

import com.redlimerl.speedrunigt.option.SpeedRunOption;
import com.redlimerl.speedrunigt.option.SpeedRunOptions;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private boolean peepoPractice$isInactive() {
        if (SpeedRunOption.getOption(SpeedRunOptions.WAITING_FIRST_INPUT).isFirstInput(InGameTimer.getInstance())) {
            return !InGameTimer.getInstance().isStarted();
        }
        return false;
    }

    @Inject(method = "canMoveVoluntarily", at = @At("RETURN"), cancellable = true)
    private void peepoPractice$cancelMovement(CallbackInfoReturnable<Boolean> cir) {
        if (this.peepoPractice$isInactive()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "playAmbientSound", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$cancelSounds(CallbackInfo ci) {
        if (this.peepoPractice$isInactive()) {
            ci.cancel();
        }
    }
}
