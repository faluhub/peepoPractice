package me.quesia.peepopractice.mixin.keybinding;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public ClientWorld world;
    @Shadow public abstract boolean isIntegratedServerRunning();
    @Shadow public abstract void openScreen(@Nullable Screen screen);

    @Inject(method = "tick", at = @At("HEAD"))
    private void listenToKeyBindings(CallbackInfo ci) {
        if (PeepoPractice.REPLAY_SPLIT_KEY.isPressed()) {
            if (this.world != null && this.isIntegratedServerRunning() && !PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
                this.openScreen(new CreateWorldScreen(null));
            }
        }
    }
}
