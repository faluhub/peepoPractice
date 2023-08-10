package me.falu.peepopractice.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategories;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @WrapWithCondition(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V", remap = false))
    private boolean peepoPractice$ignoreWarning(Logger instance, String s) {
        return PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY);
    }
}
