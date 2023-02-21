package me.quesia.peepopractice.mixin;

import me.quesia.peepopractice.core.KeyBindingHelper;
import me.quesia.peepopractice.gui.CustomOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Mutable @Shadow @Final public KeyBinding[] keysAll;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setRenderDistanceMax(MinecraftClient client, File optionsFile, CallbackInfo ci) {
        if (client.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            CustomOption.RENDER_DISTANCE.setMax(32.0f);
        } else {
            CustomOption.RENDER_DISTANCE.setMax(16.0f);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void registerKeyBindings(CallbackInfo ci) {
        this.keysAll = KeyBindingHelper.process(this.keysAll);
    }
}
