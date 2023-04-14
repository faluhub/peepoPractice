package me.quesia.peepopractice;

import me.quesia.peepopractice.core.KeyBindingHelper;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.exception.InvalidCategorySyntaxException;
import me.quesia.peepopractice.core.resource.CustomCategoryResourceManager;
import me.quesia.peepopractice.core.resource.LocalResourceManager;
import me.quesia.peepopractice.core.playerless.PlayerlessInventory;
import me.quesia.peepopractice.core.playerless.PlayerlessPlayerScreenHandler;
import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.worldpreview.WorldPreview;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PeepoPractice implements ClientModInitializer {
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("peepopractice").orElseThrow(RuntimeException::new);
    public static final String MOD_VERSION = String.valueOf(MOD_CONTAINER.getMetadata().getVersion());
    public static final String MOD_NAME = MOD_CONTAINER.getMetadata().getName();
    public static final String MOD_ID = MOD_CONTAINER.getMetadata().getId();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static PracticeCategory CATEGORY = PracticeCategories.EMPTY;
    public static PracticeCategory CONFIGURING_CATEGORY = PracticeCategories.EMPTY;
    public static PlayerlessInventory PLAYERLESS_INVENTORY;
    public static PlayerlessPlayerScreenHandler PLAYERLESS_PLAYER_SCREEN_HANDLER;
    public static final AtomicReference<ServerResourceManager> SERVER_RESOURCE_MANAGER = new AtomicReference<>();
    public static boolean RESET_CATEGORY = true;
    public static final boolean HAS_FAST_RESET = FabricLoader.getInstance().getModContainer("fast_reset").isPresent();
    public static final boolean HAS_STANDARD_SETTINGS = FabricLoader.getInstance().getModContainer("standardsettings").isPresent();
    public static final int BACKGROUND_COLOUR = BackgroundHelper.ColorMixer.getArgb(255, 68, 112, 106);
    public static final int BACKGROUND_OVERLAY_COLOUR = BackgroundHelper.ColorMixer.getArgb(60, 0, 0, 0);
    public static LevelStorage PRACTICE_LEVEL_STORAGE;
    public static boolean RETRY_PLAYER_INITIALIZATION;
    public static PlayerInventory PLAYER_INVENTORY;
    private static final String KEYBINDING_CATEGORY = KeyBindingHelper.getTranslation("key.categories." + MOD_ID, MOD_NAME).getString();
    public static KeyBinding REPLAY_SPLIT_KEY;
    public static KeyBinding NEXT_SPLIT_KEY;

    public static void log(Object message) {
        LOGGER.info(message);
    }

    @Override
    public void onInitializeClient() {
        AtomicReference<LocalResourceManager> atomicReference = new AtomicReference<>();
        Thread thread = new Thread(() -> atomicReference.get().tickTasks(), "Local Resource Manager");
        thread.setUncaughtExceptionHandler((t, throwable) -> LOGGER.error(throwable));
        atomicReference.set(new LocalResourceManager(thread));
        thread.start();

        log("Using " + MOD_NAME + " v" + MOD_VERSION);

        REPLAY_SPLIT_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        KeyBindingHelper.getTranslation("key." + MOD_ID + ".replay_split", "Replay Split").getString(),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_H,
                        KEYBINDING_CATEGORY
                )
        );
        NEXT_SPLIT_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        KeyBindingHelper.getTranslation("key." + MOD_ID + ".next_split", "Next Split").getString(),
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

    public static void disableAtumKey() {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && FabricLoader.getInstance().getModContainer("atum").isPresent()) {
            Atum.hotkeyPressed = false;
        }
    }

    public static void disableAtumReset() {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && FabricLoader.getInstance().getModContainer("atum").isPresent()) {
            Atum.isRunning = false;
        }
    }

    public static void disableWorldPreview() {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && FabricLoader.getInstance().getModContainer("worldpreview").isPresent()) {
            WorldPreview.freezePreview = true;
        }
    }

    public static PracticeCategory getNextCategory() {
        List<PracticeCategory> categories = PracticeCategories.ALL;
        categories.removeIf(PracticeCategory::isFillerCategory);
        int index = categories.indexOf(PeepoPractice.CATEGORY);
        if (categories.size() - 1 >= index + 1) {
            PracticeCategory category = categories.get(index + 1);
            if (!category.isFillerCategory()) {
                return category;
            }
        }
        return null;
    }

    public static boolean hasNextCategory() {
        return getNextCategory() != null;
    }
}
