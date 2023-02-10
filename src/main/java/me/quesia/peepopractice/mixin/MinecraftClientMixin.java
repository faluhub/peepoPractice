package me.quesia.peepopractice.mixin;

import com.mojang.datafixers.DataFixer;
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
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Final public File runDirectory;

    @Shadow @Final private DataFixer dataFixer;

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

        PeepoPractice.PRACTICE_LEVEL_STORAGE = new LevelStorage(this.runDirectory.toPath().resolve("practiceSaves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
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

    @Redirect(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/RegistryTracker$Modifiable;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"))
    private LevelStorage.Session differentSavesFolder(LevelStorage instance, String directoryName) throws IOException {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            return PeepoPractice.PRACTICE_LEVEL_STORAGE.createSession(directoryName);
        }
        return instance.createSession(directoryName);
    }

    @Inject(method = "getLevelStorage", at = @At("RETURN"), cancellable = true)
    private void customLevelStorage(CallbackInfoReturnable<LevelStorage> cir) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            cir.setReturnValue(PeepoPractice.PRACTICE_LEVEL_STORAGE);
        }
    }
}
