package me.falu.peepopractice.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.utils.InventoryUtils;
import me.falu.peepopractice.core.category.utils.StandardSettingsUtils;
import me.falu.peepopractice.core.global.GlobalOptions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final public File runDirectory;
    @Shadow @Nullable public ClientWorld world;
    @Shadow @Final public GameOptions options;
    @Shadow @Final private DataFixer dataFixer;
    @Shadow private @Nullable IntegratedServer server;

    @Shadow
    public abstract boolean isIntegratedServerRunning();
    @Shadow
    public abstract void openScreen(@Nullable Screen screen);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void peepoPractice$getResources(CallbackInfo ci) {
        PeepoPractice.PRACTICE_LEVEL_STORAGE = new LevelStorage(this.runDirectory.toPath().resolve("practiceSaves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
    }

    @Inject(method = "openScreen", at = @At("HEAD"))
    private void peepoPractice$resetCategory(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen) {
            if (PeepoPractice.RESET_CATEGORY) {
                PeepoPractice.CATEGORY = PracticeCategoriesAny.EMPTY;
                InventoryUtils.PREVIOUS_INVENTORY.clear();
            }
            PeepoPractice.RESET_CATEGORY = true;
        }
    }

    @ModifyReceiver(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/RegistryTracker$Modifiable;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"))
    private LevelStorage peepoPractice$differentSavesFolder(LevelStorage storage, String worldName) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            return PeepoPractice.PRACTICE_LEVEL_STORAGE;
        }
        return storage;
    }

    @ModifyReturnValue(method = "getLevelStorage", at = @At("RETURN"))
    private LevelStorage peepoPractice$customLevelStorage(LevelStorage storage) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            return PeepoPractice.PRACTICE_LEVEL_STORAGE;
        }
        return storage;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void peepoPractice$listenToKeyBindings(CallbackInfo ci) {
        if (this.world != null && this.isIntegratedServerRunning() && !PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            PracticeCategory nextCategory = PeepoPractice.getNextCategory();
            boolean next = PeepoPractice.NEXT_SPLIT_KEY.isPressed() && nextCategory != null;
            if (PeepoPractice.REPLAY_SPLIT_KEY.isPressed() || next) {
                if (next && (FabricLoader.getInstance().isDevelopmentEnvironment() || InGameTimer.getInstance().isCompleted())) {
                    InventoryUtils.saveCurrentPlayerInventory();
                    PeepoPractice.CATEGORY = nextCategory;
                }
                this.openScreen(new CreateWorldScreen(null));
            }
        }
    }

    @Inject(method = "method_29607", at = @At("HEAD"))
    private void peepoPractice$resetSettings1(CallbackInfo ci) {
        PeepoPractice.log("Triggered first standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }

    @Inject(method = "method_29607", at = @At("TAIL"))
    private void peepoPractice$resetSettings2(CallbackInfo ci) {
        if (!PeepoPractice.HAS_STANDARD_SETTINGS) {
            PeepoPractice.log("Triggered second standard settings call for " + PeepoPractice.CATEGORY.getId());
            StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
        }
    }

    @ModifyReturnValue(method = "getWindowTitle", at = @At("RETURN"))
    private String peepoPractice$appendTitle(String value) {
        if (GlobalOptions.CHANGE_WINDOW_TITLE.get(this.options)) {
            return value + " (Practice)";
        }
        return value;
    }

    @Inject(
            method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/RegistryTracker$Modifiable;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/integrated/IntegratedServer;isLoading()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void peepoPractice$stopLoadingTick(CallbackInfo ci, @Local LevelLoadingScreen levelLoadingScreen) {
        if (this.server == null) {
            ci.cancel();
        }
    }
}
