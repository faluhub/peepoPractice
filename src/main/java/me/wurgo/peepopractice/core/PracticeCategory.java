package me.wurgo.peepopractice.core;

import java.util.Arrays;
import java.util.List;

public enum PracticeCategory {
    Stronghold(new CategorySettings[] {
            new CategorySettings(
                    "spawnInStronghold",
                    "Spawn in Stronghold",
                    "If enabled, you'll spawn inside of the stronghold.",
                    CategoryUtils.booleanList,
                    "Enabled"
            ),
            new CategorySettings(
                    "disableMineshafts",
                    "Disable Mineshafts",
                    "If enabled, Mineshafts won't generate.",
                    CategoryUtils.booleanList,
                    "Disabled"
            ),
            new CategorySettings(
                    "disableDungeons",
                    "Disable Dungeons",
                    "If enabled, dungeons won't generate.",
                    CategoryUtils.booleanList,
                    "Disabled"
            ),
            new CategorySettings(
                    "eyeCount",
                    "Eye count",
                    "Specify the amount of eyes that are filled in.",
                    new String[] {
                            "Random",
                            "0",
                            "12"
                    },
                    "Random"
            )
    });

    public final List<CategorySettings> settings;

    PracticeCategory(CategorySettings[] settings) {
        this.settings = Arrays.asList(settings);
    }
}
