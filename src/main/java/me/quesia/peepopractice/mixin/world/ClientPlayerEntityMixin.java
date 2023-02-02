package me.quesia.peepopractice.mixin.world;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "showsDeathScreen", at = @At("RETURN"), cancellable = true)
    private void hideDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (InGameTimer.getInstance().isCompleted() && PeepoPractice.CATEGORY.hasSplitEvent()) {
                cir.setReturnValue(false);
            }
        }
    }
}
