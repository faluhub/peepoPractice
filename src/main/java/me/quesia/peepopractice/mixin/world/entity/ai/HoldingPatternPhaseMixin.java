package me.quesia.peepopractice.mixin.world.entity.ai;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.quesia.peepopractice.core.category.CategoryPreference;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(HoldingPatternPhase.class)
public abstract class HoldingPatternPhaseMixin {

    @ModifyExpressionValue(method = "method_6841", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;getNearestPathNodeIndex()I")
    ))
    private int peepoPractice$oneInEight(int randomInt) {
        if (CategoryPreference.getBoolValue("one_in_eight")) { return 0; }
        return randomInt;
    }

    @ModifyExpressionValue(method = "method_6842", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F"))
    private float peepoPractice$changeTargetHeight(float randomFloat) {
        if (CategoryPreference.getBoolValue("no_early_flyaway")) { return 0.0F; }
        return randomFloat;
    }
}