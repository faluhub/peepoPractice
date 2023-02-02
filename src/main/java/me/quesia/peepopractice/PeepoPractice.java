package me.quesia.peepopractice;

import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.resource.LocalResourceManager;
import me.quesia.peepopractice.gui.inventory.PlayerlessInventory;
import me.quesia.peepopractice.gui.inventory.PlayerlessPlayerScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.ServerResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class PeepoPractice implements ClientModInitializer {
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("peepopractice").orElseThrow(RuntimeException::new);
    public static final String MOD_VERSION = String.valueOf(MOD_CONTAINER.getMetadata().getVersion());
    public static final String MOD_NAME = MOD_CONTAINER.getMetadata().getName();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static PracticeCategory CATEGORY = PracticeCategories.EMPTY;
    public static PlayerlessInventory PLAYERLESS_INVENTORY;
    public static PlayerlessPlayerScreenHandler PLAYERLESS_PLAYER_SCREEN_HANDLER;
    public static final AtomicReference<ServerResourceManager> SERVER_RESOURCE_MANAGER = new AtomicReference<>();
    public static boolean RESET_CATEGORY = true;
    public static boolean HAS_FAST_RESET = FabricLoader.getInstance().getModContainer("fast_reset").isPresent();

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
    }
}
