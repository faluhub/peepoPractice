package me.falu.peepopractice.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategories;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @ModifyReturnValue(method = {"validatePlayerMove", "validateVehicleMove"}, at = @At("RETURN"), require = 2)
    private static boolean peepoPractice$alwaysValidMovement(boolean valid) {
        return valid && PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY);
    }
}
