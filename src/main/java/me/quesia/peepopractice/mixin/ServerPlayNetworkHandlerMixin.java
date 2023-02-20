package me.quesia.peepopractice.mixin;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "validatePlayerMove", at = @At("RETURN"), cancellable = true)
    private static void alwaysValidMovement(PlayerMoveC2SPacket packet, CallbackInfoReturnable<Boolean> cir) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "validateVehicleMove", at = @At("RETURN"), cancellable = true)
    private static void alwaysValidVehicleMovement(VehicleMoveC2SPacket packet, CallbackInfoReturnable<Boolean> cir) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            cir.setReturnValue(false);
        }
    }
}
