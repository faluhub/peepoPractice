package me.quesia.peepopractice.mixin.keybinding;

import me.quesia.peepopractice.core.KeyBindingHelper;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Mutable @Shadow @Final public KeyBinding[] keysAll;

    @Inject(method = "load", at = @At("HEAD"))
    private void registerKeyBindings(CallbackInfo ci) {
        this.keysAll = KeyBindingHelper.process(this.keysAll);
    }
}
