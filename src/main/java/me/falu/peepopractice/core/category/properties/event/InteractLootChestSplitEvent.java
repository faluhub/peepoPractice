package me.falu.peepopractice.core.category.properties.event;

import lombok.Getter;
import net.minecraft.util.Identifier;

@Getter
public class InteractLootChestSplitEvent extends SplitEvent {
    private Identifier lootTable;
    private boolean onClose = false;

    public InteractLootChestSplitEvent setLootTable(Identifier lootTable) {
        this.lootTable = lootTable;
        return this;
    }

    public boolean hasLootTable() {
        return this.lootTable != null;
    }

    public InteractLootChestSplitEvent setOnClose(boolean onClose) {
        this.onClose = onClose;
        return this;
    }
}
