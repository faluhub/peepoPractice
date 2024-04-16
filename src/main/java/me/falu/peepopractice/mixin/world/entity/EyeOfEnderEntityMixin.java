package me.falu.peepopractice.mixin.world.entity;

import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.preferences.PreferenceTypes;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyeOfEnderEntity.class)
public class EyeOfEnderEntityMixin {
    @Shadow private boolean dropsItem;

    @Inject(method = "moveTowards", at = @At("TAIL"))
    private void peepoPractice$eyeBreakPreference(BlockPos pos, CallbackInfo ci) {
        PreferenceTypes.BooleanRandomType value = CategoryPreferences.EYE_BREAKS.getValue();
        if (!value.equals(PreferenceTypes.BooleanRandomType.RANDOM)) {
            this.dropsItem = !value.equals(PreferenceTypes.BooleanRandomType.ENABLED);
        }
    }
}
