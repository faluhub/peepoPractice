package me.quesia.peepopractice.core.category;

import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PracticeCategories {
    public static List<PracticeCategory> ALL = new ArrayList<>();
    public static PracticeCategory STRONGHOLD = new PracticeCategory()
            .setId("stronghold")
            .setWorldRegistryKey(World.OVERWORLD)
            .addSetting(new CategorySetting()
                    .setId("spawn_in_stronghold")
                    .setLabel("Spawn in Stronghold")
                    .setDescription("If enabled, you'll spawn inside of the Stronghold.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.ENABLED)
            )
            .addSetting(new CategorySetting()
                    .setId("disable_mineshafts")
                    .setLabel("Disable Mineshafts")
                    .setDescription("If enabled, Mineshafts won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
            )
            .addSetting(new CategorySetting()
                    .setId("disable_dungeons")
                    .setLabel("Disable Dungeons")
                    .setDescription("If enabled, Dungeons won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
            )
            .addSetting(new CategorySetting()
                    .setId("eye_count")
                    .setLabel("Eye Count")
                    .setDescription("Specify the amount of eyes that are filled in.")
                    .addChoice("Random")
                    .addChoice("0")
                    .addChoice("12")
                    .setDefaultChoice("Random")
            );
    public static PracticeCategory OVERWORLD_TEST = new PracticeCategory()
            .setId("overworld_test")
            .setWorldRegistryKey(World.OVERWORLD);
    public static PracticeCategory NETHER_TEST = new PracticeCategory()
            .setId("nether_test")
            .setWorldRegistryKey(World.NETHER);
    public static PracticeCategory END_TEST = new PracticeCategory()
            .setId("end_test")
            .setWorldRegistryKey(World.END);
}
