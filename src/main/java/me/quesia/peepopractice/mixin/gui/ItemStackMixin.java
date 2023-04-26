package me.quesia.peepopractice.mixin.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.event.EnterVehicleSplitEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow @Nullable public abstract CompoundTag getTag();
    @Shadow public abstract Item getItem();

    @ModifyExpressionValue(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z", ordinal = 0))
    private boolean peepoPractice$ignoreItalic(boolean hasCustomName) {
        CompoundTag tag = this.getTag();
        if (hasCustomName && tag != null && tag.getBoolean("IgnoreItalic")) {
            return false;
        }
        return hasCustomName;
    }

    @Inject(method = "decrement", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$disableDecrement(int amount, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof EnterVehicleSplitEvent) {
                EnterVehicleSplitEvent event = (EnterVehicleSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasVehicle()) {
                    if (
                            event.getVehicle().equals(EntityType.BOAT) && (
                                    this.getItem().equals(Items.ACACIA_BOAT) ||
                                    this.getItem().equals(Items.BIRCH_BOAT) ||
                                    this.getItem().equals(Items.DARK_OAK_BOAT) ||
                                    this.getItem().equals(Items.OAK_BOAT) ||
                                    this.getItem().equals(Items.JUNGLE_BOAT) ||
                                    this.getItem().equals(Items.SPRUCE_BOAT)
                            )
                    ) { ci.cancel(); }
                    else if (event.getVehicle().equals(EntityType.MINECART) && this.getItem().equals(Items.MINECART)) { ci.cancel(); }
                }
            }
        }
    }
}
