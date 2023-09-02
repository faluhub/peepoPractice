package me.falu.peepopractice.mixin.gui.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OpenToLanScreen.class)
public abstract class OpenToLanScreenMixin {
    @SuppressWarnings("DefaultAnnotationParam")
    @WrapOperation(method = "method_19851", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;openToLan(Lnet/minecraft/world/GameMode;ZI)Z", remap = true), remap = false)
    private boolean peepoPractice$disableLan(IntegratedServer server, GameMode gameMode, boolean cheatsAllowed, int port, Operation<Boolean> original) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            return true;
        }
        return original.call(server, gameMode, cheatsAllowed, port);
    }
}
