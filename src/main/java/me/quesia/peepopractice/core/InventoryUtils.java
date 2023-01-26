package me.quesia.peepopractice.core;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.mixin.access.ItemEntryAccessor;
import me.quesia.peepopractice.mixin.access.LootPoolAccessor;
import me.quesia.peepopractice.mixin.access.LootTableAccessor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {
    public static final ItemGroup LOOT_TABLES = new ItemGroup(ItemGroup.HOTBAR.getIndex(), "lootTables") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.CHEST_MINECART);
        }

        @Override
        public String getTranslationKey() {
            return "Loot Tables";
        }
    };

    public static List<ItemStack> getLootTableItems(Identifier identifier) {
        List<ItemStack> list = new ArrayList<>();

        synchronized (PeepoPractice.SERVER_RESOURCE_MANAGER) {
            LootTable lootTable = PeepoPractice.SERVER_RESOURCE_MANAGER.get().getLootManager().getTable(identifier);

            for (LootPool pool : ((LootTableAccessor) lootTable).getPools()) {
                LootPoolEntry[] entries = ((LootPoolAccessor) pool).getEntries();
                outer: for (LootPoolEntry entry : entries) {
                    if (entry instanceof ItemEntry) {
                        ItemEntry itemEntry = (ItemEntry) entry;
                        Item item = ((ItemEntryAccessor) itemEntry).getItem();
                        for (ItemStack stack1 : list) {
                            if (stack1.getItem() == item) {
                                continue outer;
                            }
                        }
                        list.add(new ItemStack(item));
                    }
                }
            }
        }

        return list;
    }
}
