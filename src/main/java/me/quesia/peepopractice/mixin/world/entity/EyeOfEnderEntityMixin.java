package me.quesia.peepopractice.mixin.world.entity;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.CategoryPreference;
import me.quesia.peepopractice.core.category.utils.PracticeCategoryUtils;
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
        String value = CategoryPreference.getValue("eye_breaks");
        if (value != null && !PracticeCategoryUtils.isRandom(value)) {
            this.dropsItem = !PracticeCategoryUtils.parseBoolean(value);
        }
    }
}
