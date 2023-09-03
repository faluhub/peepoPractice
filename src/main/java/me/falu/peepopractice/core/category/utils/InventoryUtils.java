package me.falu.peepopractice.core.category.utils;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.writer.PracticeWriter;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.item.RandomToolItem;
import me.falu.peepopractice.core.playerless.PlayerlessInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {
    public static final List<ItemStack> PREVIOUS_INVENTORY = new ArrayList<>();

    public static void putItems(Inventory inventory, PracticeCategory category) {
        JsonObject config = PracticeWriter.INVENTORY_WRITER.get();
        if (config.has(category.getId())) {
            JsonObject object = config.getAsJsonObject(category.getId());
            object.entrySet().forEach(set -> {
                try {
                    CompoundTag tag = StringNbtReader.parse(set.getValue().getAsString());
                    ItemStack stack = ItemStack.fromTag(tag);
                    if (!(inventory instanceof PlayerlessInventory)) {
                        if (stack.getItem() instanceof RandomToolItem) {
                            stack = ((RandomToolItem) stack.getItem()).convert();
                        }
                    }
                    inventory.setStack(Integer.parseInt(set.getKey()), stack);
                } catch (CommandSyntaxException e) {
                    PeepoPractice.LOGGER.error(String.format("Couldn't parse inventory contents for inventory '%s'.", category.getId()), e);
                } catch (NumberFormatException e) {
                    PeepoPractice.LOGGER.error(String.format("Couldn't parse slot index: '%s' is not a valid number.", set.getKey()), e);
                }
            });
        }
    }

    public static void saveCurrentPlayerInventory() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !PeepoPractice.CATEGORY.isFillerCategory()) {
            PREVIOUS_INVENTORY.clear();
            for (int i = 0; i < client.player.inventory.size(); i++) {
                PREVIOUS_INVENTORY.add(i, client.player.inventory.getStack(i).copy());
            }
            client.player.inventory.clear();
        }
    }
}
