package me.wurgo.peepopractice.core;

import me.wurgo.peepopractice.PeepoPractice;
import me.wurgo.peepopractice.mixin.access.ItemEntryAccessor;
import me.wurgo.peepopractice.mixin.access.LootPoolAccessor;
import me.wurgo.peepopractice.mixin.access.LootTableAccessor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class InventoryUtils {
    public static ItemGroup LOOT_TABLES = new ItemGroup(ItemGroup.HOTBAR.getIndex(), "lootTables") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.CHEST_MINECART);
        }

        @Override
        public String getTranslationKey() {
            return "Loot Tables";
        }
    };

    public static ArrayList<ItemStack> getPossibleItems(Identifier identifier) {
        ArrayList<ItemStack> list = new ArrayList<>();
        LootTable lootTable = PeepoPractice.LOOT_MANAGER.getTable(identifier);

        for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
            LootPoolEntry[] entries = ((LootPoolAccessor) pool).getEntries();
            for (LootPoolEntry entry : entries) {
                if (entry instanceof ItemEntry) {
                    list.add(new ItemStack(((ItemEntryAccessor) entry).getItem()));
                }
            }
        }

        return list;
    }
}
