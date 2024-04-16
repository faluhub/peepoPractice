package me.falu.peepopractice.mixin.world.entity.ai;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.preferences.PreferenceTypes;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(HoldingPatternPhase.class)
public abstract class HoldingPatternPhaseMixin {
    @ModifyExpressionValue(
            method = "method_6841",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Random;nextInt(I)I",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;getNearestPathNodeIndex()I"
                    )
            )
    )
    private int peepoPractice$oneInEight(int randomInt) {
        PreferenceTypes.BooleanRandomType value = CategoryPreferences.ONE_IN_EIGHT.getValue();
        if (!value.equals(PreferenceTypes.BooleanRandomType.RANDOM)) {
            return value.equals(PreferenceTypes.BooleanRandomType.ENABLED) ? 0 : 1;
        }
        return randomInt;
    }

    @ModifyExpressionValue(method = "method_6842", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F"))
    private float peepoPractice$changeTargetHeight(float randomFloat) {
        if (CategoryPreferences.NO_EARLY_FLYAWAY.getBoolValue()) {
            return 0.0F;
        }
        return randomFloat;
    }
}
