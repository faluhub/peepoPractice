package me.quesia.peepopractice.core.global;

import com.google.gson.JsonPrimitive;
import me.quesia.peepopractice.core.PracticeWriter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.DoubleOption;

public class GlobalOptions {
    public static final DoubleOption BACKGROUND_RED = new DoubleOption("BG Red", 0.0D, 255.0D, 1.0F, gameOptions -> GlobalConfig.getDoubleValue("background_red", 68.0D), (gameOptions, aDouble) -> PracticeWriter.GLOBAL_CONFIG.put("background_red", new JsonPrimitive(aDouble)), (gameOptions, doubleOption) -> {
        double value = doubleOption.get(gameOptions);
        return doubleOption.getDisplayPrefix().append(Integer.toString((int) value));
    });
    public static final DoubleOption BACKGROUND_GREEN = new DoubleOption("BG Green", 0.0D, 255.0D, 1.0F, gameOptions -> GlobalConfig.getDoubleValue("background_green", 112.0D), (gameOptions, aDouble) -> PracticeWriter.GLOBAL_CONFIG.put("background_green", new JsonPrimitive(aDouble)), (gameOptions, doubleOption) -> {
        double value = doubleOption.get(gameOptions);
        return doubleOption.getDisplayPrefix().append(Integer.toString((int) value));
    });
    public static final DoubleOption BACKGROUND_BLUE = new DoubleOption("BG Blue", 0.0D, 255.0D, 1.0F, gameOptions -> GlobalConfig.getDoubleValue("background_blue", 106.0D), (gameOptions, aDouble) -> PracticeWriter.GLOBAL_CONFIG.put("background_blue", new JsonPrimitive(aDouble)), (gameOptions, doubleOption) -> {
        double value = doubleOption.get(gameOptions);
        return doubleOption.getDisplayPrefix().append(Integer.toString((int) value));
    });
    public static final BooleanOption SAME_INVENTORY = new BooleanOption("Use Same Inventory", gameOptions -> GlobalConfig.getBoolValue("same_inventory", true), (gameOptions, aBoolean) -> PracticeWriter.GLOBAL_CONFIG.put("same_inventory", new JsonPrimitive(aBoolean)));
    public static final BooleanOption CHANGE_WINDOW_TITLE = new BooleanOption("Change Window Title", gameOptions -> GlobalConfig.getBoolValue("change_window_title", true), (gameOptions, aBoolean) -> {
        PracticeWriter.GLOBAL_CONFIG.put("change_window_title", new JsonPrimitive(aBoolean));
        MinecraftClient.getInstance().updateWindowTitle();
    });
    public static final BooleanOption GIVE_SATURATION = new BooleanOption("Give Saturation", gameOptions -> GlobalConfig.getBoolValue("give_saturation", true), (gameOptions, aBoolean) -> PracticeWriter.GLOBAL_CONFIG.put("give_saturation", new JsonPrimitive(aBoolean)));
}
