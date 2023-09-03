package me.falu.peepopractice;

import me.falu.peepopractice.core.category.CustomCategoryResourceManager;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.utils.KeyBindingUtils;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.exception.InvalidCategorySyntaxException;
import me.falu.peepopractice.core.global.GlobalOptions;
import me.falu.peepopractice.core.item.RandomToolItem;
import me.falu.peepopractice.core.playerless.PlayerlessInventory;
import me.falu.peepopractice.core.playerless.PlayerlessPlayerScreenHandler;
import me.voidxwalker.autoreset.Atum;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PeepoPractice implements ModInitializer {
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("peepopractice").orElseThrow(RuntimeException::new);
    public static final String MOD_VERSION = String.valueOf(MOD_CONTAINER.getMetadata().getVersion());
    public static final String MOD_NAME = MOD_CONTAINER.getMetadata().getName();
    public static final String MOD_ID = MOD_CONTAINER.getMetadata().getId();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static PracticeCategory CATEGORY = PracticeCategoriesAny.EMPTY;
    public static PracticeCategory CONFIGURING_CATEGORY = PracticeCategoriesAny.EMPTY;
    public static PlayerlessInventory PLAYERLESS_INVENTORY;
    public static PlayerlessPlayerScreenHandler PLAYERLESS_PLAYER_SCREEN_HANDLER;
    public static boolean RESET_CATEGORY = true;
    public static final boolean HAS_FAST_RESET = FabricLoader.getInstance().getModContainer("fast_reset").isPresent();
    public static final boolean HAS_STANDARD_SETTINGS = FabricLoader.getInstance().getModContainer("standardsettings").isPresent();
    public static int[] BACKGROUND_COLOR = updateBackgroundColor();
    public static final int BACKGROUND_OVERLAY_COLOR = BackgroundHelper.ColorMixer.getArgb(60, 0, 0, 0);
    public static LevelStorage PRACTICE_LEVEL_STORAGE;
    public static boolean RETRY_PLAYER_INITIALIZATION;
    private static final String KEYBINDING_CATEGORY = KeyBindingUtils.getTranslation("key.categories." + MOD_ID, MOD_NAME).getString();
    public static KeyBinding REPLAY_SPLIT_KEY;
    public static KeyBinding NEXT_SPLIT_KEY;

    public static void log(Object message) {
        LOGGER.info(message);
    }

    @Override
    public void onInitialize() {
        log("Using " + MOD_NAME + " v" + MOD_VERSION);

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "random_pickaxe"), new RandomToolItem(RandomToolItem.ToolType.PICKAXE));
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "random_axe"), new RandomToolItem(RandomToolItem.ToolType.AXE));
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "random_shovel"), new RandomToolItem(RandomToolItem.ToolType.SHOVEL));
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "random_sword"), new RandomToolItem(RandomToolItem.ToolType.SWORD));
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "random_hoe"), new RandomToolItem(RandomToolItem.ToolType.HOE));

        REPLAY_SPLIT_KEY = KeyBindingUtils.registerKeyBinding(
                new KeyBinding(
                        KeyBindingUtils.getTranslation("key." + MOD_ID + ".replay_split", "Replay Split").getString(),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_H,
                        KEYBINDING_CATEGORY
                )
        );
        NEXT_SPLIT_KEY = KeyBindingUtils.registerKeyBinding(
                new KeyBinding(
                        KeyBindingUtils.getTranslation("key." + MOD_ID + ".next_split", "Next Split").getString(),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_J,
                        KEYBINDING_CATEGORY
                )
        );

        try { CustomCategoryResourceManager.register(); }
        catch (InvalidCategorySyntaxException e) {
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static int[] updateBackgroundColor() {
        return new int[] {
                BackgroundHelper.ColorMixer.getArgb(255, (int) GlobalOptions.BACKGROUND_RED.get(null), (int) GlobalOptions.BACKGROUND_GREEN.get(null), (int) GlobalOptions.BACKGROUND_BLUE.get(null)),
                BackgroundHelper.ColorMixer.getArgb(255, (int) GlobalOptions.BACKGROUND_RED_2.get(null), (int) GlobalOptions.BACKGROUND_GREEN_2.get(null), (int) GlobalOptions.BACKGROUND_BLUE_2.get(null))
        };
    }


    public static void drawBackground(MatrixStack matrices, Screen screen) {
        drawBackground(matrices, screen, screen);
    }

    public static void drawBackground(MatrixStack matrices, DrawableHelper drawable, Screen screen) {
        drawable.fillGradient(matrices, 0, 0, screen.width, screen.height, PeepoPractice.BACKGROUND_COLOR[0], PeepoPractice.BACKGROUND_COLOR[1]);
    }

    public static void disableAtumKey() {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY) && FabricLoader.getInstance().getModContainer("atum").isPresent()) {
            Atum.hotkeyPressed = false;
        }
    }

    public static void disableAtumReset() {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY) && FabricLoader.getInstance().getModContainer("atum").isPresent()) {
            Atum.isRunning = false;
        }
    }

    public static PracticeCategory getNextCategory() {
        List<PracticeCategory> categories = new ArrayList<>(List.copyOf(PracticeCategoriesAny.ALL));
        categories.removeIf(PracticeCategory::isFillerCategory);
        int index = categories.indexOf(PeepoPractice.CATEGORY);
        if (categories.size() - 1 >= index + 1) {
            return categories.get(index + 1);
        }
        return null;
    }

    public static boolean hasNextCategory() {
        if (PeepoPractice.CATEGORY.isAA()) { return false; }
        PracticeCategory next = getNextCategory();
        if (next != null) {
            return next.hasConfiguredInventory() || PeepoPractice.CATEGORY.isFillerCategory() || GlobalOptions.SAME_INVENTORY.get(null);
        }
        return false;
    }
}
