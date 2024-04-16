package me.falu.peepopractice.core.category.properties.event;

import lombok.Getter;
import net.minecraft.item.Item;

@Getter
public class ThrowEntitySplitEvent extends SplitEvent {
    private Item item;

    public ThrowEntitySplitEvent setItem(Item item) {
        this.item = item;
        return this;
    }

    public boolean hasItem() {
        return this.item != null;
    }
}
