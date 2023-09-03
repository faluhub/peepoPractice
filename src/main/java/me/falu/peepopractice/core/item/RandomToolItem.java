package me.falu.peepopractice.core.item;

import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;
import java.util.Random;

public class RandomToolItem extends ToolItem {
    private final ToolType toolType;

    public RandomToolItem(ToolType toolType) {
        super(ToolMaterials.DIAMOND, new Settings().group(ItemGroup.TOOLS));
        this.toolType = toolType;
    }

    public ItemStack convert() {
        String material = new Random().nextBoolean() ? "iron" : "diamond";
        Item item = Registry.ITEM.get(new Identifier(material + "_" + this.toolType.getRegistrySuffix()));
        return new ItemStack(item);
    }

    @Override
    public String getTranslationKey() {
        return "Random " + this.toolType.getDisplayName();
    }

    public enum ToolType {
        PICKAXE,
        AXE,
        SHOVEL,
        SWORD,
        HOE;

        public String getRegistrySuffix() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public String getDisplayName() {
            return this.name().toLowerCase(Locale.ROOT).replaceFirst(Character.toString(Character.toLowerCase(this.name().charAt(0))), Character.toString(Character.toUpperCase(this.name().charAt(0))));
        }
    }
}
