package me.quesia.peepopractice.mixin.gui;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private @Nullable Text title;
    @Shadow private @Nullable Text subtitle;
    @Shadow private int titleTotalTicks;
    @Shadow private int titleFadeInTicks;
    @Shadow private int titleRemainTicks;
    @Shadow private int titleFadeOutTicks;

    @Inject(method = "setTitles", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultFunc(Text text, Text text2, int i, int j, int k, CallbackInfo ci) {
        ci.cancel();

        if (text == null && text2 == null && i < 0 && j < 0 && k < 0) {
            this.title = null;
            this.subtitle = null;
            this.titleTotalTicks = 0;
            return;
        }
        if (text != null) {
            this.title = text;
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
        }
        if (text2 != null) {
            this.subtitle = text2;
        }
        if (i >= 0) {
            this.titleFadeInTicks = i;
        }
        if (j >= 0) {
            this.titleRemainTicks = j;
        }
        if (k >= 0) {
            this.titleFadeOutTicks = k;
        }
        if (this.titleTotalTicks > 0) {
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
        }
    }
}
