package me.quesia.peepopractice.core.category.properties.preset;

import me.quesia.peepopractice.core.category.CategoryPreference;
import me.quesia.peepopractice.core.category.utils.PracticeCategoryUtils;
import net.minecraft.util.Identifier;

public class FortressPreset {
    public static final CategoryPreference GOOD_BLAZE_RATES_PREFERENCE = new CategoryPreference()
            .setId("good_blaze_rates")
            .setDescription("If enabled, blazes will always drop a rod.")
            .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
            .setDefaultChoice(PracticeCategoryUtils.ENABLED)
            .setIcon(new Identifier("textures/item/blaze_rod.png"));
}
