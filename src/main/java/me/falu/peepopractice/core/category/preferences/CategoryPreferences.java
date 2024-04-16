package me.falu.peepopractice.core.category.preferences;

import net.minecraft.util.Identifier;

public class CategoryPreferences {
    public static final CategoryPreference<PreferenceTypes.BastionType> BASTION_TYPE = new CategoryPreference<PreferenceTypes.BastionType>()
            .setId("bastion_type")
            .setDefaultValue(PreferenceTypes.BastionType.RANDOM)
            .setIcon(new Identifier("textures/item/golden_helmet.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanType> RANKED_LOOT_TABLE = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("ranked_loot_table")
            .setDefaultValue(PreferenceTypes.BooleanType.ENABLED)
            .setIcon(new Identifier("textures/item/ender_pearl.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanType> ZOMBIE_PIGMEN = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("zombie_pigmen")
            .setDefaultValue(PreferenceTypes.BooleanType.ENABLED)
            .setIcon(new Identifier("textures/item/rotten_flesh.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanType> GOOD_BLAZE_RATES = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("good_blaze_rates")
            .setDefaultValue(PreferenceTypes.BooleanType.DISABLED)
            .setIcon(new Identifier("textures/item/blaze_rod.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanType> SOUL_SAND_VALLEY = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("ssv")
            .setIcon(new Identifier("textures/item/stone_sword.png"))
            .setDefaultValue(PreferenceTypes.BooleanType.DISABLED);
    public static final CategoryPreference<PreferenceTypes.BooleanType> FIRE_RESISTANCE = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("fire_resistance")
            .setDefaultValue(PreferenceTypes.BooleanType.ENABLED)
            .setIcon(new Identifier("textures/mob_effect/fire_resistance.png"));
    public static final CategoryPreference<PreferenceTypes.SpawnLocationType> SPAWN_LOCATION = new CategoryPreference<PreferenceTypes.SpawnLocationType>()
            .setId("spawn_location")
            .setIcon(new Identifier("textures/item/nether_star.png"))
            .setDefaultValue(PreferenceTypes.SpawnLocationType.STRUCTURE);
    public static final CategoryPreference<PreferenceTypes.BooleanType> FIX_GHOST_BUCKETS = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("fix_ghost_buckets")
            .setDefaultValue(PreferenceTypes.BooleanType.DISABLED)
            .setIcon(new Identifier("textures/item/water_bucket.png"));
    public static final CategoryPreference<PreferenceTypes.StrongholdDistanceType> STRONGHOLD_DISTANCE = new CategoryPreference<PreferenceTypes.StrongholdDistanceType>()
            .setId("stronghold_distance")
            .setIcon(new Identifier("textures/mob_effect/speed.png"))
            .setDefaultValue(PreferenceTypes.StrongholdDistanceType.AVERAGE);
    public static final CategoryPreference<PreferenceTypes.BooleanRandomType> EYE_BREAKS = new CategoryPreference<PreferenceTypes.BooleanRandomType>()
            .setId("eye_breaks")
            .setIcon(new Identifier("textures/item/ender_eye.png"))
            .setDefaultValue(PreferenceTypes.BooleanRandomType.RANDOM);
    public static final CategoryPreference<PreferenceTypes.BooleanType> DISABLE_MINESHAFTS = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("disable_mineshafts")
            .setDefaultValue(PreferenceTypes.BooleanType.DISABLED)
            .setIcon(new Identifier("textures/item/string.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanType> DISABLE_DUNGEONS = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("disable_dungeons")
            .setDefaultValue(PreferenceTypes.BooleanType.DISABLED)
            .setIcon(new Identifier("textures/item/spawn_egg.png"));
    public static final CategoryPreference<PreferenceTypes.EyeCountType> EYE_COUNT = new CategoryPreference<PreferenceTypes.EyeCountType>()
            .setId("eye_count")
            .setDefaultValue(PreferenceTypes.EyeCountType.RANDOM)
            .setIcon(new Identifier("textures/item/ender_eye.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanType> NO_EARLY_FLYAWAY = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("no_early_flyaway")
            .setDefaultValue(PreferenceTypes.BooleanType.ENABLED)
            .setIcon(new Identifier("textures/mob_effect/levitation.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanType> NO_CAGE_SPAWN = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("no_cage_spawn")
            .setDefaultValue(PreferenceTypes.BooleanType.DISABLED)
            .setIcon(new Identifier("textures/mob_effect/jump_boost.png"));
    public static final CategoryPreference<PreferenceTypes.StartNodeType> START_NODE = new CategoryPreference<PreferenceTypes.StartNodeType>()
            .setId("start_node")
            .setDefaultValue(PreferenceTypes.StartNodeType.RANDOM)
            .setIcon(new Identifier("textures/block/obsidian.png"));
    public static final CategoryPreference<PreferenceTypes.BooleanRandomType> ONE_IN_EIGHT = new CategoryPreference<PreferenceTypes.BooleanRandomType>()
            .setId("one_in_eight")
            .setDefaultValue(PreferenceTypes.BooleanRandomType.DISABLED)
            .setIcon(new Identifier("textures/item/brick.png"));
    public static final CategoryPreference<PreferenceTypes.TowerHeightType> TOWER_HEIGHT = new CategoryPreference<PreferenceTypes.TowerHeightType>()
            .setId("tower_height")
            .setDefaultValue(PreferenceTypes.TowerHeightType.RANDOM)
            .setIcon(new Identifier("textures/item/end_crystal.png"));
    public static final CategoryPreference<PreferenceTypes.CompareType> COMPARE_TYPE = new CategoryPreference<PreferenceTypes.CompareType>()
            .setId("compare_type")
            .setDefaultValue(PreferenceTypes.CompareType.PB)
            .setIcon(new Identifier("textures/item/clock_00.png"));
    public static final CategoryPreference<PreferenceTypes.PaceTimerShowType> PACE_TIMER_SHOW_TYPE = new CategoryPreference<PreferenceTypes.PaceTimerShowType>()
            .setId("pace_timer_show_type")
            .setDefaultValue(PreferenceTypes.PaceTimerShowType.ALWAYS)
            .setIcon(new Identifier("textures/mob_effect/blindness.png"));
    public static final CategoryPreference<PreferenceTypes.SelectedInventoryType> SELECTED_INVENTORY = new CategoryPreference<PreferenceTypes.SelectedInventoryType>()
            .setId("selected_inventory")
            .setDefaultValue(PreferenceTypes.SelectedInventoryType.ONE);
    public static final CategoryPreference<PreferenceTypes.BooleanType> SCRAMBLE_INVENTORY = new CategoryPreference<PreferenceTypes.BooleanType>()
            .setId("scramble_inventory")
            .setDefaultValue(PreferenceTypes.BooleanType.DISABLED);
}
