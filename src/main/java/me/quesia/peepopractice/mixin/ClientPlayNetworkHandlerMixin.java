package me.quesia.peepopractice.mixin;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"))
    private void ignoreWarning(Logger instance, String s) {
        if (PeepoPractice.CATEGORY == PracticeCategories.EMPTY) {
            instance.warn(s);
        }
    }
}
