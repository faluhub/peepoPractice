package me.falu.peepopractice.core.category.presets;

import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import net.minecraft.entity.effect.StatusEffects;

public class MiscPresets {
    public static final PlayerProperties.PotionEffect FIRE_RESISTANCE_EFFECT = new PlayerProperties.PotionEffect()
            .setEffect(StatusEffects.FIRE_RESISTANCE)
            .setDuration(3600)
            .setCondition((category, random, world) -> CategoryPreferences.FIRE_RESISTANCE.getBoolValue());
}
