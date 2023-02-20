package me.quesia.peepopractice.mixin.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OpenToLanScreen.class)
public class OpenToLanScreenMixin {
    @Redirect(method = "method_19851", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;openToLan(Lnet/minecraft/world/GameMode;ZI)Z"), remap = false)
    private boolean disableLan(IntegratedServer instance, GameMode gameMode, boolean cheatsAllowed, int port) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            return true;
        }
        return instance.openToLan(gameMode, cheatsAllowed, port);
    }
}
