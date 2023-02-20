package me.quesia.peepopractice.mixin.compat;

import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Keyboard.class, priority = 1005)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    public void preventAtumReset(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        PeepoPractice.disableAtumKey();
    }
}
