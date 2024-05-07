package me.falu.peepopractice.core.playerless;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class PlayerlessShulkerBoxScreenHandler extends PlayerlessScreenHandler {
    private final Inventory inventory;

    public PlayerlessShulkerBoxScreenHandler(PlayerlessInventory playerInventory) {
        this(playerInventory, new SimpleInventory(27));
    }

    private PlayerlessShulkerBoxScreenHandler(PlayerlessInventory playerInventory, Inventory inventory) {
        super();
        checkSize(inventory, 27);
        this.inventory = inventory;
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new ShulkerBoxSlot(inventory, l + k * 9, 8 + l * 18, 18 + k * 18));
            }
        }
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected static void checkSize(Inventory inventory, int expectedSize) {
        int i = inventory.size();
        if (i < expectedSize) {
            throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + expectedSize);
        }
    }

    public ListTag getItemsTag() {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(this.inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.inventory.size(); i++) {
            list.set(i, this.inventory.getStack(i));
        }
        CompoundTag tag = new CompoundTag();
        Inventories.toTag(tag, list);
        return tag.getList("Items", 10);
    }

    public void initializeItems(ItemStack itemStack) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(this.inventory.size(), ItemStack.EMPTY);
        CompoundTag tag = itemStack.getOrCreateSubTag("BlockEntityTag");
        Inventories.fromTag(tag, list);
        for (int i = 0; i < list.size(); i++) {
            this.inventory.setStack(i, list.get(i));
        }
    }

    public int size() {
        return this.inventory.size();
    }
}
