package me.quesia.peepopractice.mixin.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z", ordinal = 0))
    private boolean ignoreItalic(ItemStack instance) {
        CompoundTag tag = instance.getTag();
        if (instance.hasCustomName() && tag != null && tag.getBoolean("IgnoreItalic")) { return false; }
        return instance.hasCustomName();
    }
}
