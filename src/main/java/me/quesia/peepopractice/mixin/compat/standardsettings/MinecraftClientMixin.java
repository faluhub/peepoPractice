package me.quesia.peepopractice.mixin.compat.standardsettings;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.utils.StandardSettingsUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = -1000)
public class MinecraftClientMixin {
    @Inject(method = "method_29607", at = @At("HEAD"))
    private void resetSettings1(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        PeepoPractice.log("Triggered first standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }

    @Inject(method = "method_29607", at = @At("TAIL"))
    private void resetSettings2(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        if (!PeepoPractice.HAS_STANDARD_SETTINGS) {
            PeepoPractice.log("Triggered second standard settings call for " + PeepoPractice.CATEGORY.getId());
            StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
        }
    }

    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    private void resetSettings3(boolean focused, CallbackInfo ci) {
        PeepoPractice.log("Triggered third standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }

    @Inject(method = "onResolutionChanged", at = @At("HEAD"))
    private void resetSettings4(CallbackInfo ci) {
        PeepoPractice.log("Triggered fourth standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }
}
