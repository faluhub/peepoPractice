package me.quesia.peepopractice.gui;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.StandardSettingsUtils;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

public abstract class CustomOption {
    public static final BooleanOption CHUNK_BORDERS = new BooleanOption("Chunk Borders", gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "chunk_borders", false), (gameOptions, aBoolean) -> {
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "chunk_borders", aBoolean);
    });
    public static final DoubleOption FOV = new DoubleOption("options.fov", 30.0D, 110.0D, 1.0F, gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "fov", 70.0D), (gameOptions, aDouble) -> {
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "fov", aDouble);
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get(gameOptions);
        MutableText mutableText = doubleOption.getDisplayPrefix();
        if (d == 70.0) {
            return mutableText.append(new TranslatableText("options.fov.min"));
        }
        if (d == doubleOption.getMax()) {
            return mutableText.append(new TranslatableText("options.fov.max"));
        }
        return mutableText.append(Integer.toString((int)d));
    });
    public static final DoubleOption RENDER_DISTANCE = new DoubleOption("options.renderDistance", 2.0D, 16.0D, 1.0F, gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "render_distance", 16.0D), (gameOptions, aDouble) ->  {
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "render_distance", aDouble);
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get(gameOptions);
        return doubleOption.getDisplayPrefix().append(new TranslatableText("options.chunks", (int) d));
    });
    public static final DoubleOption ENTITY_DISTANCE_SCALING = new DoubleOption("options.entityDistanceScaling", 0.5D, 5.0D, 0.25F, gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "entity_distance", 5.0D), (gameOptions, aDouble) -> {
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "entity_distance", aDouble);
    }, (gameOptions, doubleOption) -> {
        double d = doubleOption.get(gameOptions);
        return doubleOption.getDisplayPrefix().append(new TranslatableText("options.entityDistancePercent", (int)(d * 100.0)));
    });
    public static final BooleanOption SPRINTING = new BooleanOption("Sprinting", gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "sprinting", true), (gameOptions, aBoolean) -> {
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "sprinting", aBoolean);
    });
    public static final BooleanOption FULLSCREEN = new BooleanOption("options.fullscreen", gameOptions -> StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "fullscreen", false), (gameOptions, aBoolean) -> {
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "fullscreen", aBoolean);
    });
}
