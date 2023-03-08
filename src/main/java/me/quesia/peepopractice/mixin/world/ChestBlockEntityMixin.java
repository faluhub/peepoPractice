package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.event.InteractLootChestSplitEvent;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends LootableContainerBlockEntity {
    private Identifier localLootTableId = this.lootTableId;
    private boolean valid = false;

    protected ChestBlockEntityMixin(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    private InteractLootChestSplitEvent getCorrespondingEvent(boolean close) {
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
    private void openChest(PlayerEntity player, CallbackInfo ci) {
        InteractLootChestSplitEvent event = this.getCorrespondingEvent(false);
        if (event != null) {
            event.complete(!player.isDead());
            this.valid = true;
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void closeChest(PlayerEntity player, CallbackInfo ci) {
        InteractLootChestSplitEvent event = this.getCorrespondingEvent(true);
        if (event != null) {
            event.complete(!player.isDead());
            this.valid = true;
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (!this.valid) {
            return super.createMenu(i, playerInventory, playerEntity);
        }
        return null;
    }

    @Override
    public void checkLootInteraction(@Nullable PlayerEntity player) {
        if (this.lootTableId != null) {
            this.localLootTableId = this.lootTableId;
        }
        super.checkLootInteraction(player);
    }
}
