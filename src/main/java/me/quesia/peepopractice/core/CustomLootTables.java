package me.quesia.peepopractice.core;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;

public class CustomLootTables {
    public static final LootTable CUSTOM_PIGLIN_BARTER = LootTable.builder()
            .pool(LootPool.builder()
                    .rolls(ConstantLootTableRange.create(1))
                    .with(ItemEntry.builder(Items.BOOK)
                            .weight(5)
                            .apply(new EnchantRandomlyLootFunction.Builder()
                                    .add(Enchantments.SOUL_SPEED)
                            )
                    )
                    .with(ItemEntry.builder(Items.IRON_BOOTS)
                            .weight(8)
                            .apply(new EnchantRandomlyLootFunction.Builder()
                                    .add(Enchantments.SOUL_SPEED)
                            )
                    )
                    .with(ItemEntry.builder(Items.POTION)
                            .weight(10)
                            .apply(SetNbtLootFunction.builder(Util
                                    .make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
                    )
                    .with(ItemEntry.builder(Items.SPLASH_POTION)
                            .weight(10)
                            .apply(SetNbtLootFunction.builder(Util
                                    .make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance")))))
                    .with(ItemEntry.builder(Items.IRON_NUGGET)
                            .weight(10)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(9.0F, 36.0F))
                            )
                    )
                    .with(ItemEntry.builder(Items.QUARTZ)
                            .weight(20)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(8.0F, 16.0F)))
                    )
                    .with(ItemEntry.builder(Items.GLOWSTONE_DUST)
                            .weight(20)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(5.0F, 12.0F)))
                    )
                    .with(ItemEntry.builder(Items.MAGMA_CREAM)
                            .weight(20)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(2.0F, 6.0F)))
                    )
                    .with(ItemEntry.builder(Items.STRING)
                            .weight(20)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(8.0F, 24.0F)))
                    )
                    .with(ItemEntry.builder(Items.FIRE_CHARGE)
                            .weight(40)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(1.0F, 5.0F)))
                    )
                    .with(ItemEntry.builder(Items.GRAVEL)
                            .weight(40)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(8.0F, 16.0F)))
                    )
                    .with(ItemEntry.builder(Items.LEATHER)
                            .weight(40)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(4.0F, 10.0F)))
                    )
                    .with(ItemEntry.builder(Items.NETHER_BRICK)
                            .weight(40)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(4.0F, 16.0F)))
                    )
                    .with(ItemEntry.builder(Items.CRYING_OBSIDIAN)
                            .weight(40)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(1.0F, 3.0F)))
                    )
                    .with(ItemEntry.builder(Items.SOUL_SAND)
                            .weight(40)
                            .apply(SetCountLootFunction.builder(UniformLootTableRange
                                    .between(4.0F, 16.0F)))
                    )
            )
            .build();
}
