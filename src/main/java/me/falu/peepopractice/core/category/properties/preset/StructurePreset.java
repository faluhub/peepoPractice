package me.falu.peepopractice.core.category.properties.preset;

import me.falu.peepopractice.core.category.CategoryPreference;
import me.falu.peepopractice.core.category.PracticeTypes;
import net.minecraft.util.Identifier;

public class StructurePreset {
    public static final CategoryPreference SPAWN_LOCATION_PREFERENCE = new CategoryPreference()
            .setId("spawn_location")
            .setIcon(new Identifier("textures/item/nether_star.png"))
            .setChoices(PracticeTypes.SpawnLocationType.all())
            .setDefaultChoice(PracticeTypes.SpawnLocationType.STRUCTURE.getLabel());
}
