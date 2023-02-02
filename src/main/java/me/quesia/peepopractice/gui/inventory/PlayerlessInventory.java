package me.quesia.peepopractice.gui.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import java.util.Iterator;
import java.util.List;

public class PlayerlessInventory implements Inventory, Nameable {
    public final DefaultedList<ItemStack> main;
    public final DefaultedList<ItemStack> armor;
    public final DefaultedList<ItemStack> offHand;
    private final List<DefaultedList<ItemStack>> combinedInventory;
    private ItemStack cursorStack;

    public PlayerlessInventory() {
        this.main = DefaultedList.ofSize(36, ItemStack.EMPTY);
        this.armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
        this.cursorStack = ItemStack.EMPTY;
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && this.areItemsEqual(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }

    private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
    }

    public int getEmptySlot() {
        for(int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }

        return i == -1 ? stack.getCount() : this.addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = this.getStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasTag() && stack.getTag() != null) {
                itemStack.setTag(stack.getTag().copy());
            }

            this.setStack(slot, itemStack);
        }

        int j = Math.min(i, itemStack.getMaxCount() - itemStack.getCount());

        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }

        if (j != 0) {
            i -= j;
            itemStack.increment(j);
            itemStack.setCooldown(5);
        }
        return i;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getStack(40), stack)) {
            return 40;
        } else {
            for(int i = 0; i < this.main.size(); ++i) {
                if (this.canStackAddMore(this.main.get(i), stack)) {
                    return i;
                }
            }

            return -1;
        }
    }

    public boolean insertStack(ItemStack stack) {
        return this.insertStack(-1, stack);
    }

    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        try {
            if (!stack.isDamaged()) {
                int i;
                do {
                    i = stack.getCount();
                    if (slot == -1) {
                        stack.setCount(this.addStack(stack));
                        continue;
                    }
                    stack.setCount(this.addStack(slot, stack));
                } while (!stack.isEmpty() && stack.getCount() < i);
                if (stack.getCount() == i) {
                    stack.setCount(0);
                    return true;
                }
                return stack.getCount() < i;
            }
            if (slot == -1) {
                slot = this.getEmptySlot();
            }
            if (slot >= 0) {
                this.main.set(slot, stack.copy());
                this.main.get(slot).setCooldown(5);
                stack.setCount(0);
                return true;
            }
            stack.setCount(0);
            return true;
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Adding item to inventory");
            CrashReportSection crashReportSection = crashReport.addElement("Item being added");
            crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
            crashReportSection.add("Item data", stack.getDamage());
            crashReportSection.add("Item name", () -> stack.getName().getString());
            throw new CrashException(crashReport);
        }
    }

    public ItemStack removeStack(int slot, int amount) {
        List<ItemStack> list = null;

        DefaultedList<ItemStack> defaultedList;
        for (Iterator<DefaultedList<ItemStack>> var4 = this.combinedInventory.iterator(); var4.hasNext(); slot -= defaultedList.size()) {
            defaultedList = var4.next();
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
        }

        return list != null && !list.get(slot).isEmpty() ? Inventories.splitStack(list, slot, amount) : ItemStack.EMPTY;
    }

    public ItemStack removeStack(int slot) {
        DefaultedList<ItemStack> defaultedList = null;

        DefaultedList<ItemStack> defaultedList2;
        for(Iterator<DefaultedList<ItemStack>> var3 = this.combinedInventory.iterator(); var3.hasNext(); slot -= defaultedList2.size()) {
            defaultedList2 = var3.next();
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }

        if (defaultedList != null && !defaultedList.get(slot).isEmpty()) {
            ItemStack itemStack = defaultedList.get(slot);
            defaultedList.set(slot, ItemStack.EMPTY);
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public void setStack(int slot, ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = null;

        DefaultedList<ItemStack> defaultedList2;
        for(Iterator<DefaultedList<ItemStack>> var4 = this.combinedInventory.iterator(); var4.hasNext(); slot -= defaultedList2.size()) {
            defaultedList2 = var4.next();
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }

        if (defaultedList != null) {
            defaultedList.set(slot, stack);
        }

    }

    public int size() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }

    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.main.iterator();

        ItemStack itemStack3;
        do {
            if (!var1.hasNext()) {
                var1 = this.armor.iterator();

                do {
                    if (!var1.hasNext()) {
                        var1 = this.offHand.iterator();

                        do {
                            if (!var1.hasNext()) {
                                return true;
                            }

                            itemStack3 = var1.next();
                        } while(itemStack3.isEmpty());

                        return false;
                    }

                    itemStack3 = var1.next();
                } while(itemStack3.isEmpty());

                return false;
            }

            itemStack3 = var1.next();
        } while(itemStack3.isEmpty());

        return false;
    }

    public ItemStack getStack(int slot) {
        List<ItemStack> list = null;

        DefaultedList<ItemStack> defaultedList;
        for(Iterator<DefaultedList<ItemStack>> var3 = this.combinedInventory.iterator(); var3.hasNext(); slot -= defaultedList.size()) {
            defaultedList = var3.next();
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
        }

        return list == null ? ItemStack.EMPTY : list.get(slot);
    }

    public Text getName() {
        return new TranslatableText("container.inventory");
    }

    public void markDirty() {}

    public void setCursorStack(ItemStack stack) {
        this.cursorStack = stack;
    }

    public ItemStack getCursorStack() {
        return this.cursorStack;
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    public void clear() {
        for (DefaultedList<ItemStack> itemStacks : this.combinedInventory) {
            ((List<ItemStack>) itemStacks).clear();
        }
    }
}
