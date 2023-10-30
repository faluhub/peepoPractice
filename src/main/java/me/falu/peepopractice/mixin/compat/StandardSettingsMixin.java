package me.falu.peepopractice.mixin.compat;

import com.kingcontaria.standardsettings.StandardSettings;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.utils.StandardSettingsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StandardSettings.class, remap = false)
public abstract class StandardSettingsMixin {
    @Inject(method = { "load([Ljava/lang/String;)V", "changeSettingsOnJoin" }, at = @At("TAIL"), require = 2)
    private static void peepoPractice$customSettings1(CallbackInfo ci) {
        PeepoPractice.log("Triggered second standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }
}
