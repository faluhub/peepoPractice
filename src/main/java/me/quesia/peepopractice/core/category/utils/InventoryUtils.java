package me.quesia.peepopractice.core.category.utils;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.mixin.access.ItemEntryAccessor;
import me.quesia.peepopractice.mixin.access.LootPoolAccessor;
import me.quesia.peepopractice.mixin.access.LootTableAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
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
    public static final List<ItemStack> PREVIOUS_INVENTORY = new ArrayList<>();

    public static List<ItemStack> getLootTableItems(Identifier identifier) {
        List<ItemStack> list = new ArrayList<>();

        synchronized (PeepoPractice.SERVER_RESOURCE_MANAGER) {
            LootTable lootTable = PeepoPractice.SERVER_RESOURCE_MANAGER.get().getLootManager().getTable(identifier);
            for (LootPool pool : ((LootTableAccessor) lootTable).peepoPractice$getPools()) {
                LootPoolEntry[] entries = ((LootPoolAccessor) pool).peepoPractice$getEntries();
                outer: for (LootPoolEntry entry : entries) {
                    if (entry instanceof ItemEntry) {
                        ItemEntry itemEntry = (ItemEntry) entry;
                        Item item = ((ItemEntryAccessor) itemEntry).peepoPractice$getItem();
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

    public static void putItems(Inventory inventory, PracticeCategory category) {
        JsonObject config = PracticeWriter.INVENTORY_WRITER.get();
        if (config.has(category.getId())) {
            JsonObject object = config.getAsJsonObject(category.getId());
            object.entrySet().forEach(set -> {
                try {
                    CompoundTag tag = StringNbtReader.parse(set.getValue().getAsString());
                    ItemStack stack = ItemStack.fromTag(tag);
                    inventory.setStack(Integer.parseInt(set.getKey()), stack);
                } catch (CommandSyntaxException ignored) {
                    PeepoPractice.LOGGER.error("Couldn't parse inventory contents for inventory '{}'.", category.getId());
                } catch (NumberFormatException ignored) {
                    PeepoPractice.LOGGER.error("Couldn't parse slot index: '{}' is not a valid number.", set.getKey());
                }
            });
        }
    }

    public static void saveCurrentPlayerInventory() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !PeepoPractice.CATEGORY.isFillerCategory()) {
            PeepoPractice.log("DEBUG saved");
            PREVIOUS_INVENTORY.clear();
            for (int i = 0; i < client.player.inventory.size(); i++) {
                PREVIOUS_INVENTORY.add(i, client.player.inventory.getStack(i).copy());
            }
            client.player.inventory.clear();
        }
    }
}
