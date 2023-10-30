package me.falu.peepopractice.mixin.world;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.properties.event.InteractLootChestSplitEvent;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends LootableContainerBlockEntityMixin {
    @Unique private Identifier localLootTableId = this.lootTableId;
    @Unique private boolean valid = false;

    @Unique
    private InteractLootChestSplitEvent peepoPractice$getCorrespondingEvent(boolean close) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof InteractLootChestSplitEvent) {
                InteractLootChestSplitEvent event = (InteractLootChestSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasLootTable() && event.getLootTable().equals(this.localLootTableId)) {
                    if ((event.isOnClose() && close) || (!event.isOnClose() && !close)) {
                        return event;
                    }
                }
            }
        }
        return null;
    }

    @Inject(method = "onOpen", at = @At("HEAD"))
    private void peepoPractice$openChest(PlayerEntity player, CallbackInfo ci) {
        InteractLootChestSplitEvent event = this.peepoPractice$getCorrespondingEvent(false);
        if (event != null) {
            event.complete(!player.isDead());
            this.valid = true;
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void peepoPractice$closeChest(PlayerEntity player, CallbackInfo ci) {
        InteractLootChestSplitEvent event = this.peepoPractice$getCorrespondingEvent(true);
        if (event != null) {
            event.complete(!player.isDead());
            this.valid = true;
        }
    }

    @Override
    protected void peepoPractice$onCreateMenu(CallbackInfoReturnable<ScreenHandler> cir) {
        if (this.valid) {
            cir.setReturnValue(null);
        }
    }

    @Override
    protected void peepoPractice$onCheckLootInteraction(CallbackInfo ci) {
        if (this.lootTableId != null) {
            this.localLootTableId = this.lootTableId;
        }
    }
}
