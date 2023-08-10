package me.falu.peepopractice.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.falu.peepopractice.PeepoPractice;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @ModifyReturnValue(method = "showsDeathScreen", at = @At("RETURN"))
    private boolean peepoPractice$noDeathScreen(boolean showsDeathScreen) {
        return showsDeathScreen && !PeepoPractice.CATEGORY.hasSplitEvent();
    }
}
