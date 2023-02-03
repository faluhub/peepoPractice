package me.quesia.peepopractice.core.category.properties.event;

import net.minecraft.item.Item;

public class ThrowEntitySplitEvent extends SplitEvent {
    private Item item;

    public Item getItem() {
        return this.item;
    }

    public boolean hasItem() {
        return this.item != null;
    }

    public ThrowEntitySplitEvent setItem(Item item) {
        this.item = item;
        return this;
    }
}
