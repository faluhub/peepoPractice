package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.event.InteractLootableContainerSplitEvent;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin extends LockableContainerBlockEntity {
    @Shadow @Nullable protected Identifier lootTableId;

    protected LootableContainerBlockEntityMixin(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    private InteractLootableContainerSplitEvent getCorrespondingEvent() {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof InteractLootableContainerSplitEvent) {
                InteractLootableContainerSplitEvent event = (InteractLootableContainerSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasBlockEntityType() && event.getBlockEntityType().equals(this.getType())) {
                    if (event.hasLootTable() && event.getLootTable().equals(this.lootTableId)) {
                        return event;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        InteractLootableContainerSplitEvent event = this.getCorrespondingEvent();
        if (event != null) {
            event.complete(!playerEntity.isDead());
            return null;
        }
        return super.createMenu(i, playerInventory, playerEntity);
    }
}
