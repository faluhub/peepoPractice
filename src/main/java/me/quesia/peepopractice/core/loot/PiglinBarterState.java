package me.quesia.peepopractice.core.loot;

import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

import java.util.List;
import java.util.Random;

public class PiglinBarterState extends PersistentState {
    private int currentGuarantee;
    private int obsidianCount;
    private int pearlCount;
    private int pearl;
    private int obsidian;

    public PiglinBarterState() {
        super("piglin_barters");
        this.currentGuarantee = 0;
        this.pearlCount = 3;
        this.pearl = 0;
        this.obsidianCount = 6;
        this.obsidian = 0;
    }

    public ItemStack guaranteeItem(PiglinEntity piglin, ItemStack itemStack, Random random) {
        ItemStack newItem = guaranteeItem2(piglin, itemStack, random);
        this.currentGuarantee++;
        return newItem;
    }

    private ItemStack guaranteeItem2(PiglinEntity piglin, ItemStack itemStack, Random random) {
        if (this.currentGuarantee == 72) {
            this.currentGuarantee = 0;
            this.pearl = 0;
            this.obsidian = 0;
            this.pearlCount = 3;
            this.obsidianCount = 6;
        }
        if (this.pearl == 0 && this.pearlCount > 0){
            rollPearlIndex(random);
        }
        if (this.obsidian == 0 && this.obsidianCount > 0) {
            rollObsidianIndex(random);
        }
        if (itemStack.getItem() == Items.ENDER_PEARL) {
            if (this.pearlCount < 0) {
                List<ItemStack> newBarterItem = PiglinBrain.getBarteredItem(piglin);
                return guaranteeItem2(piglin, newBarterItem.get(0), random);
            }
            rollPearlIndex(random);
            return itemStack;
        }
        if (itemStack.getItem() == Items.OBSIDIAN) {
            rollObsidianIndex(random);
            return itemStack;
        }
        if (this.pearl <= this.currentGuarantee && this.pearlCount >= 0) {
            rollPearlIndex(random);
            return new ItemStack(Items.ENDER_PEARL, random.nextInt(5) + 4);
        }
        if (this.obsidian <= this.currentGuarantee && this.obsidianCount >= 0) {
            rollObsidianIndex(random);
            return new ItemStack(Items.OBSIDIAN);
        }
        return itemStack;
    }

    public void rollPearlIndex(Random random) {
        this.pearl = random.nextInt(Math.max(1, 72 - this.currentGuarantee - this.pearlCount)) + this.currentGuarantee;
        this.pearlCount--;
    }

    public void rollObsidianIndex(Random random) {
        this.obsidian = random.nextInt(Math.max(1, 72 - this.currentGuarantee - this.obsidianCount)) + this.currentGuarantee;
        this.obsidianCount--;
    }

    public void fromTag(CompoundTag tag) {
        this.obsidian = tag.getInt("obsidian");
        this.obsidianCount = tag.getInt("obsidianCount");
        this.pearl = tag.getInt("pearl");
        this.pearlCount = tag.getInt("pearlCount");
        this.currentGuarantee = tag.getInt("currentGuarantee");
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("obsidian", this.obsidian);
        tag.putInt("obsidianCount", this.obsidianCount);
        tag.putInt("pearl", this.pearl);
        tag.putInt("pearlCount", this.pearlCount);
        tag.putInt("currentGuarantee", this.currentGuarantee);
        return tag;
    }
}
