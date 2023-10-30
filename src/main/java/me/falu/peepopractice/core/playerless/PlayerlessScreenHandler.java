package me.falu.peepopractice.core.playerless;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

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
        if (slotActionType == SlotActionType.QUICK_CRAFT) {
            int k = this.quickCraftButton;
            this.quickCraftButton = ScreenHandler.unpackQuickCraftStage(j);
            if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
                this.endQuickCraft();
            } else if (playerInventory.getCursorStack().isEmpty()) {
                this.endQuickCraft();
            } else if (this.quickCraftButton == 0) {
                this.quickCraftStage = ScreenHandler.unpackQuickCraftButton(j);
                if (shouldQuickCraftContinue(this.quickCraftStage)) {
                    this.quickCraftButton = 1;
                    this.quickCraftSlots.clear();
                } else {
                    this.endQuickCraft();
                }
            } else if (this.quickCraftButton == 1) {
                Slot slot = this.slots.get(i);
                ItemStack itemStack2 = playerInventory.getCursorStack();
                if (slot != null && ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && slot.canInsert(itemStack2) && (this.quickCraftStage == 2 || itemStack2.getCount() > this.quickCraftSlots.size())) {
                    this.quickCraftSlots.add(slot);
                }
            } else if (this.quickCraftButton == 2) {
                if (!this.quickCraftSlots.isEmpty()) {
                    ItemStack itemStack3 = playerInventory.getCursorStack().copy();
                    int l = playerInventory.getCursorStack().getCount();
                    for (Slot slot2 : this.quickCraftSlots) {
                        ItemStack itemStack4 = playerInventory.getCursorStack();
                        if (slot2 == null || !ScreenHandler.canInsertItemIntoSlot(slot2, itemStack4, true) || !slot2.canInsert(itemStack4) || this.quickCraftStage != 2 && itemStack4.getCount() < this.quickCraftSlots.size()) continue;
                        ItemStack itemStack5 = itemStack3.copy();
                        int m = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                        ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, m);
                        int n = Math.min(itemStack5.getMaxCount(), slot2.getMaxStackAmount(itemStack5));
                        if (itemStack5.getCount() > n) {
                            itemStack5.setCount(n);
                        }
                        l -= itemStack5.getCount() - m;
                        slot2.setStack(itemStack5);
                    }
                    itemStack3.setCount(l);
                    playerInventory.setCursorStack(itemStack3);
                }
                this.endQuickCraft();
            } else {
                this.endQuickCraft();
            }
        } else if (this.quickCraftButton != 0) {
            this.endQuickCraft();
        } else if (!(slotActionType != SlotActionType.PICKUP && slotActionType != SlotActionType.QUICK_MOVE || j != 0 && j != 1)) {
            if (i == -999) {
                if (!playerInventory.getCursorStack().isEmpty()) {
                    if (j == 0) {
                        playerInventory.setCursorStack(ItemStack.EMPTY);
                    }
                    if (j == 1) {
                        playerInventory.getCursorStack().split(1);
                    }
                }
            } else if (slotActionType == SlotActionType.QUICK_MOVE) {
                if (i < 0) {
                    return;
                }
                Slot slot3 = this.slots.get(i);
                if (slot3 == null) {
                    return;
                }
                ItemStack itemStack3 = this.transferSlot(i);
                while (!itemStack3.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot3.getStack(), itemStack3)) {
                    itemStack3 = this.transferSlot(i);
                }
            } else {
                if (i < 0) {
                    return;
                }
                Slot slot3 = this.slots.get(i);
                if (slot3 != null) {
                    ItemStack itemStack3 = slot3.getStack();
                    ItemStack itemStack2 = playerInventory.getCursorStack();
                    if (itemStack3.isEmpty()) {
                        if (!itemStack2.isEmpty() && slot3.canInsert(itemStack2)) {
                            int o = j == 0 ? itemStack2.getCount() : 1;
                            if (o > slot3.getMaxStackAmount(itemStack2)) {
                                o = slot3.getMaxStackAmount(itemStack2);
                            }
                            slot3.setStack(itemStack2.split(o));
                        }
                    } else {
                        int o;
                        if (itemStack2.isEmpty()) {
                            int o2 = j == 0 ? itemStack3.getCount() : (itemStack3.getCount() + 1) / 2;
                            playerInventory.setCursorStack(slot3.takeStack(o2));
                            if (itemStack3.isEmpty()) {
                                slot3.setStack(ItemStack.EMPTY);
                            }
                        } else if (slot3.canInsert(itemStack2)) {
                            if (ScreenHandler.canStacksCombine(itemStack3, itemStack2)) {
                                int o3 = j == 0 ? itemStack2.getCount() : 1;
                                if (o3 > slot3.getMaxStackAmount(itemStack2) - itemStack3.getCount()) {
                                    o3 = slot3.getMaxStackAmount(itemStack2) - itemStack3.getCount();
                                }
                                if (o3 > itemStack2.getMaxCount() - itemStack3.getCount()) {
                                    o3 = itemStack2.getMaxCount() - itemStack3.getCount();
                                }
                                itemStack2.decrement(o3);
                                itemStack3.increment(o3);
                            } else if (itemStack2.getCount() <= slot3.getMaxStackAmount(itemStack2)) {
                                slot3.setStack(itemStack2);
                                playerInventory.setCursorStack(itemStack3);
                            }
                        } else if (itemStack2.getMaxCount() > 1 && ScreenHandler.canStacksCombine(itemStack3, itemStack2) && !itemStack3.isEmpty() && (o = itemStack3.getCount()) + itemStack2.getCount() <= itemStack2.getMaxCount()) {
                            itemStack2.increment(o);
                            itemStack3 = slot3.takeStack(o);
                            if (itemStack3.isEmpty()) {
                                slot3.setStack(ItemStack.EMPTY);
                            }
                        }
                    }
                    slot3.markDirty();
                }
            }
        } else if (slotActionType == SlotActionType.SWAP) {
            Slot slot3 = this.slots.get(i);
            ItemStack itemStack3 = playerInventory.getStack(j);
            ItemStack itemStack2 = slot3.getStack();
            if (!itemStack3.isEmpty() || !itemStack2.isEmpty()) {
                if (itemStack3.isEmpty()) {
                    playerInventory.setStack(j, itemStack2);
                    slot3.setStack(ItemStack.EMPTY);
                } else if (itemStack2.isEmpty()) {
                    if (slot3.canInsert(itemStack3)) {
                        int o = slot3.getMaxStackAmount(itemStack3);
                        if (itemStack3.getCount() > o) {
                            slot3.setStack(itemStack3.split(o));
                        } else {
                            slot3.setStack(itemStack3);
                            playerInventory.setStack(j, ItemStack.EMPTY);
                        }
                    }
                } else if (slot3.canInsert(itemStack3)) {
                    int o = slot3.getMaxStackAmount(itemStack3);
                    if (itemStack3.getCount() > o) {
                        slot3.setStack(itemStack3.split(o));
                        playerInventory.insertStack(itemStack2);
                    } else {
                        slot3.setStack(itemStack3);
                        playerInventory.setStack(j, itemStack2);
                    }
                }
            }
        } else if (slotActionType == SlotActionType.CLONE && playerInventory.getCursorStack().isEmpty() && i >= 0) {
            Slot slot3 = this.slots.get(i);
            if (slot3 != null && slot3.hasStack()) {
                ItemStack itemStack3 = slot3.getStack().copy();
                itemStack3.setCount(itemStack3.getMaxCount());
                playerInventory.setCursorStack(itemStack3);
            }
        } else if (slotActionType == SlotActionType.THROW && playerInventory.getCursorStack().isEmpty() && i >= 0) {
            Slot slot3 = this.slots.get(i);
            if (slot3 != null && slot3.hasStack()) {
                slot3.takeStack(j == 0 ? 1 : slot3.getStack().getCount());
            }
        } else if (slotActionType == SlotActionType.PICKUP_ALL && i >= 0) {
            Slot slot3 = this.slots.get(i);
            ItemStack itemStack3 = playerInventory.getCursorStack();
            if (!(itemStack3.isEmpty() || slot3 != null && slot3.hasStack())) {
                int l = j == 0 ? 0 : this.slots.size() - 1;
                int o = j == 0 ? 1 : -1;
                for (int p = 0; p < 2; ++p) {
                    for (int q = l; q >= 0 && q < this.slots.size() && itemStack3.getCount() < itemStack3.getMaxCount(); q += o) {
                        Slot slot4 = this.slots.get(q);
                        if (!slot4.hasStack() || !ScreenHandler.canInsertItemIntoSlot(slot4, itemStack3, true)) continue;
                        ItemStack itemStack6 = slot4.getStack();
                        if (p == 0 && itemStack6.getCount() == itemStack6.getMaxCount()) continue;
                        int n = Math.min(itemStack3.getMaxCount() - itemStack3.getCount(), itemStack6.getCount());
                        ItemStack itemStack7 = slot4.takeStack(n);
                        itemStack3.increment(n);
                        if (itemStack7.isEmpty()) {
                            slot4.setStack(ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    public ItemStack transferSlot(int index) {
        Slot slot = this.slots.get(index);
        if (slot != null) {
            return slot.getStack();
        }
        return ItemStack.EMPTY;
    }

    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        ItemStack itemStack;
        Slot slot;
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (!itemStack.isEmpty() && ScreenHandler.canStacksCombine(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= stack.getMaxCount()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        slot.markDirty();
                        bl = true;
                    } else if (itemStack.getCount() < stack.getMaxCount()) {
                        stack.decrement(stack.getMaxCount() - itemStack.getCount());
                        itemStack.setCount(stack.getMaxCount());
                        slot.markDirty();
                        bl = true;
                    }
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        if (!stack.isEmpty()) {
            i = fromLast ? endIndex - 1 : startIndex;
            while (fromLast ? i >= startIndex : i < endIndex) {
                slot = this.slots.get(i);
                itemStack = slot.getStack();
                if (itemStack.isEmpty() && slot.canInsert(stack)) {
                    if (stack.getCount() > slot.getMaxStackAmount()) {
                        slot.setStack(stack.split(slot.getMaxStackAmount()));
                    } else {
                        slot.setStack(stack.split(stack.getCount()));
                    }
                    slot.markDirty();
                    bl = true;
                    break;
                }
                if (fromLast) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        return !bl;
    }

    public static boolean shouldQuickCraftContinue(int stage) {
        return stage >= 0 && stage <= 2;
    }

    protected void endQuickCraft() {
        this.quickCraftButton = 0;
        this.quickCraftSlots.clear();
    }
}
