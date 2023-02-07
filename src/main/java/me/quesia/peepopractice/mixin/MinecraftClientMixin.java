package me.quesia.peepopractice.mixin;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.StandardSettingsUtils;
import me.quesia.peepopractice.core.resource.LocalResourceManager;
import me.quesia.peepopractice.mixin.access.ThreadExecutorAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;runTasks()V"))
    private void runMoreTasks(MinecraftClient instance) {
        ((ThreadExecutorAccessor) instance).invokeRunTasks();
        LocalResourceManager.INSTANCE.submit(() -> LocalResourceManager.INSTANCE.tickTasks());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void getResources(CallbackInfo ci) {
        PeepoPractice.log("Reloading local resource manager...");

        LocalResourceManager.INSTANCE.submit(() -> {
            LocalResourceManager.INSTANCE.reload().whenComplete((serverResourceManager, throwable) -> {
                if (throwable != null) {
                    PeepoPractice.LOGGER.error(throwable);
                    return;
                }

                PeepoPractice.log("Done reloading local resource manager.");
                synchronized (PeepoPractice.SERVER_RESOURCE_MANAGER) {
                    PeepoPractice.SERVER_RESOURCE_MANAGER.set(serverResourceManager);
                    PeepoPractice.SERVER_RESOURCE_MANAGER.notifyAll();
                }
            });
        });
    }

    @Inject(method = "openScreen", at = @At("HEAD"))
    private void resetCategory(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen) {
            PeepoPractice.CATEGORY.reset();
            if (PeepoPractice.RESET_CATEGORY) {
                PeepoPractice.CATEGORY = PracticeCategories.EMPTY;
            }
        }
    }

    @Inject(method = "method_29607", at = @At("HEAD"))
    private void standardSettings_resetSettings(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        PeepoPractice.log("Triggered first standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }

    @Inject(method = "method_29607", at = @At("TAIL"))
    private void standardSettings_onWorldLoad(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        PeepoPractice.log("Triggered second standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }
}
