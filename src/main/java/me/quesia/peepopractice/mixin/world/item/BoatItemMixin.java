package me.quesia.peepopractice.mixin.world.item;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.event.EnterVehicleSplitEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatItem.class)
public class BoatItemMixin {
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private void peepoPractice$keepBoat(ItemStack instance, int amount) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof EnterVehicleSplitEvent) {
                EnterVehicleSplitEvent event = (EnterVehicleSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasVehicle() && event.getVehicle().equals(EntityType.BOAT) && event.shouldKeepItem()) {
                    return;
                }
            }
        }
        instance.decrement(amount);
    }
}
