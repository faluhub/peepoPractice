package me.wurgo.peepopractice;

import com.google.gson.JsonElement;
import me.wurgo.peepopractice.core.PracticeCategory;
import me.wurgo.peepopractice.core.resource.JsonDataReader;
import me.wurgo.peepopractice.core.resource.ResourceDataType;
import me.wurgo.peepopractice.core.resource.manager.SimpleLootConditionManager;
import me.wurgo.peepopractice.core.resource.manager.SimpleLootManager;
import me.wurgo.peepopractice.gui.inventory.PlayerlessInventory;
import me.wurgo.peepopractice.gui.inventory.PlayerlessPlayerScreenHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class PeepoPractice {
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("peepopractice").get();
    public static final String MOD_VERSION = String.valueOf(MOD_CONTAINER.getMetadata().getVersion());
    public static final String LOGGER_NAME = MOD_CONTAINER.getMetadata().getName();
    public static Logger LOGGER = LogManager.getLogger(LOGGER_NAME);

    public static PracticeCategory CATEGORY;
    public static PlayerlessInventory PLAYERLESS_INVENTORY;
    public static PlayerlessPlayerScreenHandler PLAYERLESS_PLAYER_SCREEN_HANDLER;
    public static SimpleLootConditionManager LOOT_CONDITION_MANAGER = new SimpleLootConditionManager();
    public static SimpleLootManager LOOT_MANAGER = new SimpleLootManager(LOOT_CONDITION_MANAGER);

    public static void log(Object message) {
        LOGGER.info(message);
    }

    static {
        LOOT_CONDITION_MANAGER.reload();
        LOOT_MANAGER.reload();
    }
}
