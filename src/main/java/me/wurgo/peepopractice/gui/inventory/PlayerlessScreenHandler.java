package me.wurgo.peepopractice.gui.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class PlayerlessScreenHandler {
    private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
    public final List<Slot> slots = Lists.newArrayList();
    private int quickCraftStage = -1;
    private int quickCraftButton;
    private final Set<Slot> quickCraftSlots = Sets.newHashSet();

    protected void addSlot(Slot slot) {
        slot.id = this.slots.size();
        this.slots.add(slot);
        this.trackedStacks.add(ItemStack.EMPTY);
    }

    public Slot getSlot(int index) {
        return this.slots.get(index);
    }

    public ItemStack transferSlot(int index) {
        Slot slot = this.slots.get(index);
        return slot != null ? slot.getStack() : ItemStack.EMPTY;
    }

    public void onSlotClick(int i, int j, SlotActionType actionType, PlayerlessInventory inventory) {
        try {
            this.method_30010(i, j, actionType, inventory);
        } catch (Exception var8) {
            CrashReport crashReport = CrashReport.create(var8, "Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", i);
            crashReportSection.add("Button", j);
            crashReportSection.add("Type", actionType);
            throw new CrashException(crashReport);
        }
    }

    private void method_30010(int i, int j, SlotActionType slotActionType, PlayerlessInventory playerInventory) {
        ItemStack itemStack7;
        ItemStack itemStack8;
        int n;
        int l;
        if (slotActionType == SlotActionType.QUICK_CRAFT) {
            int k = this.quickCraftButton;
            this.quickCraftButton = unpackQuickCraftStage(j);
            if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
                this.endQuickCraft();
            } else if (playerInventory.getCursorStack().isEmpty()) {
                this.endQuickCraft();
            } else if (this.quickCraftButton == 0) {
                this.quickCraftStage = unpackQuickCraftButton(j);
                if (shouldQuickCraftContinue(this.quickCraftStage)) {
                    this.quickCraftButton = 1;
                    this.quickCraftSlots.clear();
                } else {
                    this.endQuickCraft();
                }
            } else if (this.quickCraftButton == 1) {
                Slot slot = this.slots.get(i);
                itemStack8 = playerInventory.getCursorStack();
                if (slot != null && canInsertItemIntoSlot(slot, itemStack8, true) && slot.canInsert(itemStack8) && (this.quickCraftStage == 2 || itemStack8.getCount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot()) {
                    this.quickCraftSlots.add(slot);
                }
            } else if (this.quickCraftButton == 2) {
                if (!this.quickCraftSlots.isEmpty()) {
                    itemStack7 = playerInventory.getCursorStack().copy();
                    l = playerInventory.getCursorStack().getCount();
                    Iterator<Slot> var23 = this.quickCraftSlots.iterator();

                    label336:
                    while(true) {
                        Slot slot2;
                        ItemStack itemStack4;
                        do {
                            do {
                                do {
                                    do {
                                        if (!var23.hasNext()) {
                                            itemStack7.setCount(l);
                                            playerInventory.setCursorStack(itemStack7);
                                            break label336;
                                        }

                                        slot2 = var23.next();
                                        itemStack4 = playerInventory.getCursorStack();
                                    } while(slot2 == null);
                                } while(!canInsertItemIntoSlot(slot2, itemStack4, true));
                            } while(!slot2.canInsert(itemStack4));
                        } while(this.quickCraftStage != 2 && itemStack4.getCount() < this.quickCraftSlots.size());

                        if (this.canInsertIntoSlot()) {
                            ItemStack itemStack5 = itemStack7.copy();
                            int m = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                            calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, m);
                            n = Math.min(itemStack5.getMaxCount(), slot2.getMaxStackAmount(itemStack5));
                            if (itemStack5.getCount() > n) {
                                itemStack5.setCount(n);
                            }

                            l -= itemStack5.getCount() - m;
                            slot2.setStack(itemStack5);
                        }
                    }
                }

                this.endQuickCraft();
            } else {
                this.endQuickCraft();
            }
        } else if (this.quickCraftButton != 0) {
            this.endQuickCraft();
        } else {
            Slot slot4;
            int q;
            if ((slotActionType == SlotActionType.PICKUP || slotActionType == SlotActionType.QUICK_MOVE) && (j == 0 || j == 1)) {
                if (i == -999) {
                    if (!playerInventory.getCursorStack().isEmpty()) {
                        if (j == 0) {
                            playerInventory.setCursorStack(ItemStack.EMPTY);
                        }
                    }
                } else if (slotActionType == SlotActionType.QUICK_MOVE) {
                    if (i < 0) {
                        return;
                    }

                    slot4 = this.slots.get(i);
                    if (slot4 == null) {
                        return;
                    }

                    for(itemStack7 = this.transferSlot(i); !itemStack7.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot4.getStack(), itemStack7); itemStack7 = this.transferSlot(i)) {
                    }
                } else {
                    if (i < 0) {
                        return;
                    }

                    slot4 = this.slots.get(i);
                    if (slot4 != null) {
                        itemStack7 = slot4.getStack();
                        itemStack8 = playerInventory.getCursorStack();

                        if (itemStack7.isEmpty() && itemStack7.getCount() == 0) {
                            if (!itemStack8.isEmpty() && slot4.canInsert(itemStack8)) {
                                q = j == 0 ? itemStack8.getCount() : 1;
                                if (q > slot4.getMaxStackAmount(itemStack8)) {
                                    q = slot4.getMaxStackAmount(itemStack8);
                                }

                                slot4.setStack(itemStack8.split(q));
                            }
                        } else {
                            if (itemStack8.isEmpty()) {
                                q = j == 0 ? itemStack7.getCount() : (itemStack7.getCount() + 1) / 2;
                                playerInventory.setCursorStack(slot4.takeStack(q));
                                if (itemStack7.isEmpty()) {
                                    slot4.setStack(ItemStack.EMPTY);
                                }
                            } else if (slot4.canInsert(itemStack8)) {
                                if (canStacksCombine(itemStack7, itemStack8)) {
                                    q = j == 0 ? itemStack8.getCount() : 1;
                                    if (q > slot4.getMaxStackAmount(itemStack8) - itemStack7.getCount()) {
                                        q = slot4.getMaxStackAmount(itemStack8) - itemStack7.getCount();
                                    }

                                    if (q > itemStack8.getMaxCount() - itemStack7.getCount()) {
                                        q = itemStack8.getMaxCount() - itemStack7.getCount();
                                    }

                                    itemStack8.decrement(q);
                                    itemStack7.increment(q);
                                } else if (itemStack8.getCount() <= slot4.getMaxStackAmount(itemStack8)) {
                                    slot4.setStack(itemStack8);
                                    playerInventory.setCursorStack(itemStack7);
                                }
                            } else if (itemStack8.getMaxCount() > 1 && canStacksCombine(itemStack7, itemStack8) && !itemStack7.isEmpty()) {
                                q = itemStack7.getCount();
                                if (q + itemStack8.getCount() <= itemStack8.getMaxCount()) {
                                    itemStack8.increment(q);
                                    itemStack7 = slot4.takeStack(q);
                                    if (itemStack7.isEmpty()) {
                                        slot4.setStack(ItemStack.EMPTY);
                                    }
                                }
                            }
                        }

                        slot4.markDirty();
                    }
                }
            } else if (slotActionType == SlotActionType.SWAP) {
                slot4 = this.slots.get(i);
                itemStack7 = playerInventory.getStack(j);
                itemStack8 = slot4.getStack();
                if (!itemStack7.isEmpty() || !itemStack8.isEmpty()) {
                    if (itemStack7.isEmpty()) {
                        playerInventory.setStack(j, itemStack8);
                        slot4.setStack(ItemStack.EMPTY);
                    } else if (itemStack8.isEmpty()) {
                        if (slot4.canInsert(itemStack7)) {
                            q = slot4.getMaxStackAmount(itemStack7);
                            if (itemStack7.getCount() > q) {
                                slot4.setStack(itemStack7.split(q));
                            } else {
                                slot4.setStack(itemStack7);
                                playerInventory.setStack(j, ItemStack.EMPTY);
                            }
                        }
                    } else if (slot4.canInsert(itemStack7)) {
                        q = slot4.getMaxStackAmount(itemStack7);
                        if (itemStack7.getCount() > q) {
                            slot4.setStack(itemStack7.split(q));
                            playerInventory.insertStack(itemStack8);
                        } else {
                            slot4.setStack(itemStack7);
                            playerInventory.setStack(j, itemStack8);
                        }
                    }
                }
            } else if (slotActionType == SlotActionType.CLONE && playerInventory.getCursorStack().isEmpty() && i >= 0) {
                slot4 = this.slots.get(i);
                if (slot4 != null && slot4.hasStack()) {
                    itemStack7 = slot4.getStack().copy();
                    itemStack7.setCount(itemStack7.getMaxCount());
                    playerInventory.setCursorStack(itemStack7);
                }
            } else if (slotActionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && i >= 0) {
                slot4 = this.slots.get(i);
                if (slot4 != null && slot4.hasStack()) {
                    slot4.takeStack(j == 0 ? 1 : slot4.getStack().getCount());
                }
            } else if (slotActionType == SlotActionType.PICKUP_ALL && i >= 0) {
                slot4 = this.slots.get(i);
                itemStack7 = playerInventory.getCursorStack();
                if (!itemStack7.isEmpty() && (slot4 == null || !slot4.hasStack())) {
                    l = j == 0 ? 0 : this.slots.size() - 1;
                    q = j == 0 ? 1 : -1;

                    for(int w = 0; w < 2; ++w) {
                        for(int x = l; x >= 0 && x < this.slots.size() && itemStack7.getCount() < itemStack7.getMaxCount(); x += q) {
                            Slot slot9 = this.slots.get(x);
                            if (slot9.hasStack() && canInsertItemIntoSlot(slot9, itemStack7, true) && this.canInsertIntoSlot(itemStack7, slot9)) {
                                ItemStack itemStack14 = slot9.getStack();
                                if (w != 0 || itemStack14.getCount() != itemStack14.getMaxCount()) {
                                    n = Math.min(itemStack7.getMaxCount() - itemStack7.getCount(), itemStack14.getCount());
                                    ItemStack itemStack15 = slot9.takeStack(n);
                                    itemStack7.increment(n);
                                    if (itemStack15.isEmpty()) {
                                        slot9.setStack(ItemStack.EMPTY);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static boolean canStacksCombine(ItemStack first, ItemStack second) {
        return first.getItem() == second.getItem() && ItemStack.areTagsEqual(first, second);
    }

    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return true;
    }

    public static int unpackQuickCraftButton(int quickCraftData) {
        return quickCraftData >> 2 & 3;
    }

    public static int unpackQuickCraftStage(int quickCraftData) {
        return quickCraftData & 3;
    }

    public static boolean shouldQuickCraftContinue(int stage) {
        return stage >= 0 && stage <= 2;
    }

    protected void endQuickCraft() {
        this.quickCraftButton = 0;
        this.quickCraftSlots.clear();
    }

    public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
        boolean bl = slot == null || !slot.hasStack();
        if (!bl && stack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), stack)) {
            return slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount();
        } else {
            return bl;
        }
    }

    public static void calculateStackSize(Set<Slot> slots, int mode, ItemStack stack, int stackSize) {
        switch(mode) {
            case 0:
                stack.setCount(MathHelper.floor((float)stack.getCount() / (float)slots.size()));
                break;
            case 1:
                stack.setCount(1);
                break;
            case 2:
                stack.setCount(stack.getItem().getMaxCount());
        }

        stack.increment(stackSize);
    }

    public boolean canInsertIntoSlot() {
        return true;
    }
}
