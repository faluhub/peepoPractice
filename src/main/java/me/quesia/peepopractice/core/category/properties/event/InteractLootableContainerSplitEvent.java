package me.quesia.peepopractice.core.category.properties.event;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

public class InteractLootableContainerSplitEvent extends SplitEvent {
    private BlockEntityType<? extends BlockEntity> blockEntityType;
    private Identifier lootTable;

    public BlockEntityType<? extends BlockEntity> getBlockEntityType() {
        return this.blockEntityType;
    }

    public boolean hasBlockEntityType() {
        return this.blockEntityType != null;
    }

    public InteractLootableContainerSplitEvent setBlockEntityType(BlockEntityType<? extends BlockEntity> blockEntityType) {
        this.blockEntityType = blockEntityType;
        return this;
    }

    public Identifier getLootTable() {
        return this.lootTable;
    }

    public boolean hasLootTable() {
        return this.lootTable != null;
    }

    public InteractLootableContainerSplitEvent setLootTable(Identifier lootTable) {
        this.lootTable = lootTable;
        return this;
    }
}
