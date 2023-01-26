package me.quesia.peepopractice.core;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum PracticeCategory {
    Stronghold(new CategorySettings[] {
            new CategorySettings(
                    "spawnInStronghold",
                    "Spawn in Stronghold",
                    "If enabled, you'll spawn inside of the stronghold.",
                    CategoryUtils.BOOLEAN_LIST,
                    "Enabled"
            ),
            new CategorySettings(
                    "disableMineshafts",
                    "Disable Mineshafts",
                    "If enabled, Mineshafts won't generate.",
                    CategoryUtils.BOOLEAN_LIST,
                    "Disabled"
            ),
            new CategorySettings(
                    "disableDungeons",
                    "Disable Dungeons",
                    "If enabled, dungeons won't generate.",
                    CategoryUtils.BOOLEAN_LIST,
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

    public final String id;
    public final List<CategorySettings> settings;

    PracticeCategory(CategorySettings[] settings) {
        this.id = name().toLowerCase(Locale.ROOT);
        this.settings = Arrays.asList(settings);
    }
}
