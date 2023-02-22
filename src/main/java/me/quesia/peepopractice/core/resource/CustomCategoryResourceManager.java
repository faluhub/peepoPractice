package me.quesia.peepopractice.core.resource;

import com.google.gson.*;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.properties.PlayerProperties;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import me.quesia.peepopractice.core.category.properties.WorldProperties;
import me.quesia.peepopractice.core.category.properties.event.*;
import me.quesia.peepopractice.core.exception.InvalidCategorySyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class CustomCategoryResourceManager {
    private static final File CATEGORIES_FOLDER = FabricLoader.getInstance().getConfigDir().resolve(PeepoPractice.MOD_NAME).resolve("categories").toFile();

    public static void register() throws InvalidCategorySyntaxException {
        try {
            if (CATEGORIES_FOLDER.exists()) {
                if (CATEGORIES_FOLDER.isFile()) {
                    Files.delete(CATEGORIES_FOLDER.toPath());
                }
            }
            boolean ignored = CATEGORIES_FOLDER.mkdirs();

            DirectoryStream.Filter<Path> filter = entry -> entry.toFile().isFile() && entry.getFileName().toString().endsWith(".json");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(CATEGORIES_FOLDER.toPath(), filter)) {
                StringBuilder categories = new StringBuilder("Registered custom categories: ");
                for (Path path : stream) {
                    PeepoPractice.log(path.toString());
                    Object obj;

                    try {
                        FileReader reader = new FileReader(path.toFile());
                        JsonParser parser = new JsonParser();
                        obj = parser.parse(reader);
                        reader.close();
                    } catch (JsonParseException e) {
                        throw new InvalidCategorySyntaxException(path.toString());
                    }

                    if (obj != null && !obj.equals(JsonNull.INSTANCE)) {
                        JsonObject main = (JsonObject) obj;
                        PracticeCategory category = new PracticeCategory(true);

                        if (main.has("id")) {
                            category = category.setId(main.get("id").getAsString());
                        }
                        if (main.has("can_have_empty_inventory")) {
                            category = category.setCanHaveEmptyInventory(main.get("can_have_empty_inventory").getAsBoolean());
                        }
                        if (main.has("hidden")) {
                            category = category.setHidden(main.get("hidden").getAsBoolean());
                        }
                        if (main.has("player_properties")) {
                            JsonObject playerProperties = main.get("player_properties").getAsJsonObject();
                            PlayerProperties properties = new PlayerProperties();
                            if (playerProperties.has("spawn_pos")) {
                                JsonArray spawnPos = playerProperties.get("spawn_pos").getAsJsonArray();
                                properties = properties.setSpawnPos(new BlockPos(spawnPos.get(0).getAsDouble(), spawnPos.get(1).getAsDouble(), spawnPos.get(2).getAsDouble()));
                            }
                            if (playerProperties.has("spawn_angle")) {
                                JsonArray spawnAngle = playerProperties.get("spawn_angle").getAsJsonArray();
                                properties = properties.setSpawnAngle(spawnAngle.get(0).getAsFloat(), spawnAngle.get(1).getAsFloat());
                            }
                            if (playerProperties.has("vehicle")) {
                                String vehicleId = playerProperties.get("vehicle").getAsString();
                                properties = properties.setVehicle(Registry.ENTITY_TYPE.get(new Identifier(vehicleId)));
                            }
                            category = category.setPlayerProperties(properties);
                        }
                        if (main.has("structure_properties")) {
                            JsonArray array = main.get("structure_properties").getAsJsonArray();
                            for (JsonElement element : array) {
                                if (element instanceof JsonObject) {
                                    JsonObject structureProperties = (JsonObject) element;
                                    StructureProperties properties = new StructureProperties();
                                    if (structureProperties.has("chunk_pos")) {
                                        JsonArray chunkPos = structureProperties.get("chunk_pos").getAsJsonArray();
                                        properties = properties.setChunkPos(new ChunkPos(chunkPos.get(0).getAsInt(), chunkPos.get(1).getAsInt()));
                                    }
                                    if (structureProperties.has("orientation")) {
                                        String orientationName = structureProperties.get("orientation").getAsString();
                                        properties = properties.setOrientation(Direction.byName(orientationName));
                                    }
                                    if (structureProperties.has("rotation")) {
                                        String rotationName = structureProperties.get("rotationName").getAsString();
                                        properties = properties.setRotation(BlockRotation.valueOf(rotationName.toUpperCase(Locale.ROOT)));
                                    }
                                    if (structureProperties.has("structure_top_y")) {
                                        properties = properties.setStructureTopY(structureProperties.get("structure_top_y").getAsInt());
                                    }
                                    if (structureProperties.has("generatable")) {
                                        properties = properties.setGeneratable(structureProperties.get("generatable").getAsBoolean());
                                    }
                                    category = category.addStructureProperties(properties);
                                }
                            }
                        }
                        if (main.has("world_properties")) {
                            JsonObject worldProperties = main.get("world_properties").getAsJsonObject();
                            WorldProperties properties = new WorldProperties();
                            if (worldProperties.has("world_registry_key")) {
                                String registryKeyName = worldProperties.get("world_registry_key").getAsString().toUpperCase(Locale.ROOT);
                                RegistryKey<World> registryKey;
                                switch (registryKeyName) {
                                    default:
                                    case "OVERWORLD":
                                        registryKey = World.OVERWORLD;
                                        break;
                                    case "NETHER":
                                        registryKey = World.NETHER;
                                        break;
                                    case "END":
                                        registryKey = World.END;
                                        break;
                                }
                                properties = properties.setWorldRegistryKey(registryKey);
                            }
                            if (worldProperties.has("spawn_chunks_disabled")) {
                                properties = properties.setSpawnChunksDisabled(worldProperties.get("spawn_chunks_disabled").getAsBoolean());
                            }
                            if (worldProperties.has("anti_biome_map")) {
                                JsonArray array = worldProperties.get("anti_biome_map").getAsJsonArray();
                                for (JsonElement element : array) {
                                    if (element instanceof JsonObject) {
                                        JsonObject antiBiomeInfo = (JsonObject) element;
                                        if (antiBiomeInfo.has("biome") && antiBiomeInfo.has("range")) {
                                            Biome biome = Registry.BIOME.get(new Identifier(antiBiomeInfo.get("biome").getAsString()));
                                            Integer range = antiBiomeInfo.get("range").getAsInt();
                                            range = range > 0 ? range : null;
                                            properties = properties.addAntiBiomeRange(biome, range);
                                        }
                                    }
                                }
                            }
                            if (worldProperties.has("pro_biome_map")) {
                                JsonArray array = worldProperties.get("pro_biome_map").getAsJsonArray();
                                for (JsonElement element : array) {
                                    if (element instanceof JsonObject) {
                                        JsonObject antiBiomeInfo = (JsonObject) element;
                                        if (antiBiomeInfo.has("biome") && antiBiomeInfo.has("range")) {
                                            Biome biome = Registry.BIOME.get(new Identifier(antiBiomeInfo.get("biome").getAsString()));
                                            Integer range = antiBiomeInfo.get("range").getAsInt();
                                            range = range > 0 ? range : null;
                                            properties = properties.addAntiBiomeRange(biome, range);
                                        }
                                    }
                                }
                            }
                            category = category.setWorldProperties(properties);
                        }
                        if (main.has("split_event")) {
                            JsonObject splitEvent = main.get("split_event").getAsJsonObject();
                            String eventId = splitEvent.get("id").getAsString().toLowerCase(Locale.ROOT);
                            switch (eventId) {
                                case "change_dimension":
                                    if (splitEvent.has("dimension")) {
                                        ChangeDimensionSplitEvent event = new ChangeDimensionSplitEvent();
                                        String registryKeyName = splitEvent.get("dimension").getAsString().toUpperCase(Locale.ROOT);
                                        RegistryKey<World> registryKey;
                                        switch (registryKeyName) {
                                            default:
                                            case "OVERWORLD":
                                                registryKey = World.OVERWORLD;
                                                break;
                                            case "NETHER":
                                                registryKey = World.NETHER;
                                                break;
                                            case "END":
                                                registryKey = World.END;
                                                break;
                                        }
                                        event = event.setDimension(registryKey);
                                        category = category.setSplitEvent(event);
                                    }
                                    break;
                                case "get_advancement":
                                    if (splitEvent.has("advancement")) {
                                        GetAdvancementSplitEvent event = new GetAdvancementSplitEvent();
                                        event = event.setAdvancement(new Identifier(splitEvent.get("advancement").getAsString()));
                                        category = category.setSplitEvent(event);
                                    }
                                    break;
                                case "interact_lootable_container":
                                    if (splitEvent.has("block_entity_type") && splitEvent.has("loot_table")) {
                                        InteractLootableContainerSplitEvent event = new InteractLootableContainerSplitEvent();
                                        BlockEntityType<? extends BlockEntity> blockEntityType = Registry.BLOCK_ENTITY_TYPE.get(new Identifier(splitEvent.get("block_entity_type").getAsString()));
                                        Identifier lootTable = new Identifier(splitEvent.get("loot_table").getAsString());
                                        event = event.setBlockEntityType(blockEntityType);
                                        event = event.setLootTable(lootTable);
                                        category = category.setSplitEvent(event);
                                    }
                                    break;
                                case "throw_entity":
                                    if (splitEvent.has("item")) {
                                        ThrowEntitySplitEvent event = new ThrowEntitySplitEvent();
                                        Item item = Registry.ITEM.get(new Identifier(splitEvent.get("item").getAsString()));
                                        event = event.setItem(item);
                                        category = category.setSplitEvent(event);
                                    }
                                    break;
                            }
                        }
                        category.register();
                        categories.append(category.getName(false)).append(", ");
                    }
                }
                PeepoPractice.log(categories);
            }
        } catch (IOException e) {
            throw new InvalidCategorySyntaxException();
        }
    }
}
