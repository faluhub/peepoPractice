package me.quesia.peepopractice.mixin.compat.timer;

import com.redlimerl.speedrunigt.timer.PracticeTimerManager;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PracticeTimerManager.class, remap = false)
public class PracticeTimerManagerMixin {
    @Inject(method = "startPractice", at = @At("HEAD"), cancellable = true)
    private static void disablePracticeTimer(float offsetTime, CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            ci.cancel();
        }
    }
}
