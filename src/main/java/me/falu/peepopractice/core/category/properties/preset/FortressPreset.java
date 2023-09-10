package me.falu.peepopractice.core.category.properties.preset;

import me.falu.peepopractice.core.category.CategoryPreference;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

public class FortressPreset {
    public static final CategoryPreference GOOD_BLAZE_RATES_PREFERENCE = new CategoryPreference()
            .setId("good_blaze_rates")
            .setDescription("If enabled, blazes will always drop a rod.")
            .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
            .setDefaultChoice(PracticeCategoryUtils.ENABLED)
            .setIcon(new Identifier("textures/item/blaze_rod.png"));
    public static final CategoryPreference SOUL_SAND_VALLEY_PREFERENCE = new CategoryPreference()
            .setId("ssv")
            .setIcon(new Identifier("textures/item/stone_sword.png"))
            .setLabel("Soul Sand Valley")
            .setDescription("If enabled, the fortress will always generate in a soul sand valley biome.")
            .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
            .setDefaultChoice(PracticeCategoryUtils.DISABLED);
    public static final CategoryPreference FIRE_RESISTANCE_PREFERENCE = new CategoryPreference()
            .setId("fire_resistance")
            .setDescription("Start with Fire Resistance status effect.")
            .setIcon(new Identifier("textures/mob_effect/fire_resistance.png"))
            .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
            .setDefaultChoice(PracticeCategoryUtils.ENABLED);
    public static final PlayerProperties.PotionEffect FIRE_RESISTANCE_EFFECT = new PlayerProperties.PotionEffect()
            .setEffect(StatusEffects.FIRE_RESISTANCE)
            .setDuration(3600)
            .setCondition((category, random, world) -> CategoryPreference.getBoolValue("fire_resistance"));
}
