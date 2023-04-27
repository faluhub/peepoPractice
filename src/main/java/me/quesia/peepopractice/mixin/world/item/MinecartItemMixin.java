package me.quesia.peepopractice.mixin.world.item;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.event.EnterVehicleSplitEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecartItem.class)
public class MinecartItemMixin {
    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private void peepoPractice$keepBoat(ItemStack instance, int amount) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof EnterVehicleSplitEvent) {
                EnterVehicleSplitEvent event = (EnterVehicleSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasVehicle() && event.getVehicle().equals(EntityType.MINECART) && event.shouldKeepItem()) {
                    return;
                }
            }
        }
        instance.decrement(amount);
    }
}
