package me.quesia.peepopractice.mixin.compat;

import com.kingcontaria.standardsettings.StandardSettings;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.utils.StandardSettingsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StandardSettings.class, remap = false)
public class StandardSettingsMixin {
    @Inject(method = "load([Ljava/lang/String;)V", at = @At("TAIL"))
    private static void customSettings1(String[] lines, CallbackInfo ci) {
        PeepoPractice.log("Triggered second standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }

    @Inject(method = "changeSettingsOnJoin", at = @At("TAIL"))
    private static void customSettings2(CallbackInfo ci) {
        PeepoPractice.log("Triggered second standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }
}
