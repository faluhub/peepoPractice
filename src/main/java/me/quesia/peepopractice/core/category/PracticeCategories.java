package me.quesia.peepopractice.core.category;

import java.util.ArrayList;
import java.util.List;

public class PracticeCategories {
    public static List<PracticeCategory> ALL = new ArrayList<>();
    public static PracticeCategory STRONGHOLD = new PracticeCategory("stronghold")
            .addSetting(new CategorySettings.Builder()
                    .setId("spawnInStronghold")
                    .setLabel("Spawn in Stronghold")
                    .setDescription("If enabled, you'll spawn inside of the Stronghold.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.ENABLED)
                    .build()
            )
            .addSetting(new CategorySettings.Builder()
                    .setId("disableMineshafts")
                    .setLabel("Disable Mineshafts")
                    .setDescription("If enabled, Mineshafts won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                    .build()
            )
            .addSetting(new CategorySettings.Builder()
                    .setId("disableDungeons")
                    .setLabel("Disable Dungeons")
                    .setDescription("If enabled, Dungeons won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                    .build()
            )
            .addSetting(new CategorySettings.Builder()
                    .setId("eyeCount")
                    .setLabel("Eye Count")
                    .setDescription("Specify the amount of eyes that are filled in.")
                    .addChoice("Random")
                    .addChoice("0")
                    .addChoice("12")
                    .setDefaultChoice("Random")
                    .build()
            );
}
