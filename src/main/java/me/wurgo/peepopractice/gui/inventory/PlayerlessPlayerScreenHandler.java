package me.wurgo.peepopractice.gui.inventory;

import com.mojang.datafixers.util.Pair;
import me.wurgo.peepopractice.PeepoPractice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class PlayerlessPlayerScreenHandler extends PlayerlessScreenHandler {
    public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");
    public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
    public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = new Identifier("item/empty_armor_slot_shield");
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES;
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER;
    public final boolean onServer;

    public PlayerlessPlayerScreenHandler() {
        this.onServer = false;
        this.addSlot(new EditInventoryScreen.LockedSlot(PeepoPractice.playerlessInventory, 0, 154, 28));

        int n;
        int m;
        for(n = 0; n < 2; ++n) {
            for(m = 0; m < 2; ++m) {
                this.addSlot(new EditInventoryScreen.LockedSlot(PeepoPractice.playerlessInventory, m + n * 2, 98 + m * 18, 18 + n * 18));
            }
        }

        for(n = 0; n < 4; ++n) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[n];
            this.addSlot(new Slot(PeepoPractice.playerlessInventory, 39 - n, 8, 8 + n * 18) {
                public int getMaxStackAmount() {
                    return 1;
                }

                public boolean canInsert(ItemStack stack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
                }

                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    return itemStack.isEmpty() || !EnchantmentHelper.hasBindingCurse(itemStack);
                }

                @Environment(EnvType.CLIENT)
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerlessPlayerScreenHandler.EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                }
            });
        }

        for (n = 0; n < 3; ++n) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(PeepoPractice.playerlessInventory, m + (n + 1) * 9, 8 + m * 18, 84 + n * 18));
            }
        }

        for (n = 0; n < 9; ++n) {
            this.addSlot(new Slot(PeepoPractice.playerlessInventory, n, 8 + n * 18, 142));
        }

        this.addSlot(new Slot(PeepoPractice.playerlessInventory, 40, 77, 62) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerlessPlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerlessPlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }

    static {
        EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
        EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }
}
