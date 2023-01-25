package me.wurgo.peepopractice.mixin.gui;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z", ordinal = 0))
    private boolean ignoreItalic(ItemStack instance) {
        if (instance.getTag() != null && instance.getTag().getBoolean("IgnoreItalic")) {
            return false;
        }
        return instance.hasCustomName();
    }
}
