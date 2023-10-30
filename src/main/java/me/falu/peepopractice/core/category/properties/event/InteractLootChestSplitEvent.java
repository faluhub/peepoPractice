package me.falu.peepopractice.core.category.properties.event;

import net.minecraft.util.Identifier;

public class InteractLootChestSplitEvent extends SplitEvent {
    private Identifier lootTable;
    private boolean onClose = false;

    public Identifier getLootTable() {
        return this.lootTable;
    }

    public InteractLootChestSplitEvent setLootTable(Identifier lootTable) {
        this.lootTable = lootTable;
        return this;
    }

    public boolean hasLootTable() {
        return this.lootTable != null;
    }

    public boolean isOnClose() {
        return this.onClose;
    }

    public InteractLootChestSplitEvent setOnClose(boolean onClose) {
        this.onClose = onClose;
        return this;
    }
}
