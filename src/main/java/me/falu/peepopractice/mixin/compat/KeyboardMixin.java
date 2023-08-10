package me.falu.peepopractice.mixin.compat;

import me.falu.peepopractice.PeepoPractice;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Keyboard.class, priority = 1005)
public abstract class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void peepoPractice$preventAtumReset(CallbackInfo ci) {
        PeepoPractice.disableAtumKey();
    }
}
