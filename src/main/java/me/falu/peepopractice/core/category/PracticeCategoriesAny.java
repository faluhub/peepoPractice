package me.falu.peepopractice.core.category;

import com.google.common.collect.Lists;
import me.falu.peepopractice.core.CustomPortalForcer;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.preferences.PreferenceTypes;
import me.falu.peepopractice.core.category.presets.MiscPresets;
import me.falu.peepopractice.core.category.presets.TaskPresets;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.category.properties.event.*;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import me.falu.peepopractice.core.exception.NotInitializedException;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PracticeCategoriesAny {
    public static final List<PracticeCategory> ALL = new ArrayList<>();
    public static final PracticeCategory EMPTY = new PracticeCategory()
            .setId("empty")
            .setFillerCategory(true)
            .setHidden(true);
    public static final PracticeCategory MAPLESS_SPLIT = new PracticeCategory()
            .setId("mapless_split")
            .setCanHaveEmptyInventory(true)
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos((category, random, world) -> {
                                BlockPos spawnPos = null;
                                BlockPos btPos = null;
                                do {
                                    if (btPos == null) {
                                        btPos = new BlockPos(0, 0, 0);
                                    }
                                    btPos = world.locateStructure(StructureFeature.BURIED_TREASURE, new BlockPos(-btPos.getX() * 1.5D, 0, -btPos.getZ() * 1.5D), 100, false);
                                    if (btPos != null) {
                                        BiomeSource biomeSource = world.getChunkManager().getChunkGenerator().getBiomeSource();
                                        List<Biome> biomes = Lists.newArrayList(Biomes.BEACH, Biomes.SNOWY_BEACH);
                                        BlockPos pos = biomeSource.locateBiome(btPos.getX(), world.getChunkManager().getChunkGenerator().getSpawnHeight(), btPos.getZ(), 42, biomes, random);
                                        if (pos != null) {
                                            spawnPos = world.getRandomPosInChunk(pos.getX(), pos.getY(), pos.getZ(), 15);
                                            spawnPos = SpawnLocating.findServerSpawnPoint(world, new ChunkPos(spawnPos), false);
                                        }
                                    }
                                } while (spawnPos == null);
                                return spawnPos;
                            })
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.BEACHED_SHIPWRECK)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.SUNKEN_SHIPWRECK)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.COLD_OCEAN_RUIN)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.WARM_OCEAN_RUIN)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.NORMAL_MINESHAFT)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.DESERT_VILLAGE)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.PLAINS_VILLAGE)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.SAVANNA_VILLAGE)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.TAIGA_VILLAGE)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.SNOWY_VILLAGE)
                            .setGeneratable(false)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.OVERWORLD)
            )
            .setSplitEvent(
                    new InteractLootChestSplitEvent()
                            .setLootTable(LootTables.BURIED_TREASURE_CHEST)
                            .setOnClose(true)
            )
            .register();
    public static final PracticeCategory ISLAND_LEAVE_SPLIT = new PracticeCategory()
            .setId("island_leave_split")
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos((category, random, world) -> {
                                BlockPos lastPos = null;
                                int mx = random.nextBoolean() ? 1 : -1;
                                int mz = random.nextBoolean() ? 1 : -1;
                                while (true) {
                                    if (lastPos == null) {
                                        lastPos = new BlockPos(0, 0, 0);
                                    }
                                    BiomeSource biomeSource = world.getChunkManager().getChunkGenerator().getBiomeSource();
                                    List<Biome> biomes = Lists.newArrayList(Biomes.BEACH, Biomes.SNOWY_BEACH);
                                    BlockPos pos = biomeSource.locateBiome(lastPos.getX(), world.getChunkManager().getChunkGenerator().getSpawnHeight(), lastPos.getZ(), 999, biomes, random);
                                    if (pos != null) {
                                        BlockPos tempPos = world.getRandomPosInChunk(pos.getX(), pos.getY(), pos.getZ(), 15);
                                        tempPos = SpawnLocating.findServerSpawnPoint(world, new ChunkPos(tempPos), true);
                                        if (tempPos != null) {
                                            ChunkPos spawnChunkPos = new ChunkPos(tempPos.add(0, 0, 16));
                                            for (int x = 0; x < 16; x++) {
                                                for (int z = 0; z < 16; z++) {
                                                    Chunk chunk = world.getChunk(spawnChunkPos.x, spawnChunkPos.z, ChunkStatus.FULL);
                                                    BlockPos iterPos = spawnChunkPos.toBlockPos(x, 0, z);
                                                    BlockState state = chunk.getBlockState(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, iterPos).down());
                                                    if (state.getBlock() instanceof LeavesBlock) {
                                                        return tempPos;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    lastPos = lastPos.add(random.nextInt(100, 200) * mx, 0, random.nextInt(100, 200) * mz);
                                }
                            })
            ).setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.OVERWORLD)
            ).setSplitEvent(
                    new EnterVehicleSplitEvent()
                            .setVehicle(EntityType.BOAT)
                            .setKeepItem(true)
            )
            .register();
    public static final PracticeCategory RAVINE_ENTER_SPLIT = new PracticeCategory()
            .setId("ravine_enter_split")
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos((category, random, world) -> {
                                if (category.hasCustomValue("ravinePosition")) {
                                    BlockPos ravinePos = (BlockPos) category.getCustomValue("ravinePosition");
                                    BlockPos pos = new BlockPos(ravinePos.getX(), world.getChunkManager().getChunkGenerator().getSeaLevel(), ravinePos.getZ());
                                    return PracticeCategoryUtils.getRandomBlockInRadius(20, 5, pos, random);
                                }
                                throw new NotInitializedException();
                            })
                            .setSpawnAngle((category, random, world) -> {
                                if (category.hasPlayerProperties() && category.getPlayerProperties().hasSpawnPos()) {
                                    if (category.hasCustomValue("ravinePosition")) {
                                        BlockPos targetPos = (BlockPos) category.getCustomValue("ravinePosition");
                                        BlockPos spawnPos = category.getPlayerProperties().getSpawnPos();
                                        float yaw = (float) Math.toDegrees(Math.atan((float) (spawnPos.getZ() - targetPos.getZ()) / (spawnPos.getX() - targetPos.getX())));
                                        return new Vec2f(yaw + 90, 45);
                                    }
                                }
                                throw new NotInitializedException();
                            })
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.MONUMENT)
                            .setGeneratable(false)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.OVERWORLD)
                            .addProBiome(
                                    new WorldProperties.BiomeModification()
                                            .setBiome(Biomes.DEEP_OCEAN)
                                            .setRange(new WorldProperties.Range()
                                                              .setRange(null)
                                                              .addValidDimension(World.OVERWORLD)
                                            )
                            )
            )
            .setSplitEvent(
                    new ChangeDimensionSplitEvent()
                            .setFromDimension(World.OVERWORLD)
                            .setToDimension(World.NETHER)
            )
            .addPreference(CategoryPreferences.FIX_GHOST_BUCKETS)
            .register();
    public static final PracticeCategory BASTION_SPLIT = new PracticeCategory()
            .setId("bastion_split")
            .setFillerCategory(true)
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                            .setChunkPos((category, random, world) -> {
                                PreferenceTypes.SpawnLocationType spawnLocation = CategoryPreferences.SPAWN_LOCATION.getValue();
                                if (spawnLocation.equals(PreferenceTypes.SpawnLocationType.TERRAIN)) {
                                    int mx = random.nextBoolean() ? 1 : -1;
                                    int mz = random.nextBoolean() ? 1 : -1;
                                    return new ChunkPos(random.nextInt(3, 4) * mx, random.nextInt(3, 4) * mz);
                                }
                                return new ChunkPos(0, 0);
                            })
                            .setRotation(BlockRotation.NONE)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.FORTRESS)
                            .setGeneratable(false)
            )
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos(TaskPresets.BASTION_SPAWN_POS)
                            .setSpawnAngle(TaskPresets.BASTION_SPAWN_ANGLE)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.NETHER)
                            .addAntiBiome(new WorldProperties.BiomeModification()
                                                  .setBiome(Biomes.BASALT_DELTAS)
                                                  .setReplacement(Biomes.NETHER_WASTES)
                                                  .setRange(new WorldProperties.Range()
                                                                    .setRange(null)
                                                                    .addValidDimension(World.NETHER)
                                                  )
                            )
            )
            .setSplitEvent(
                    new ThrowEntitySplitEvent()
                            .setItem(Items.ENDER_PEARL)
            )
            .addPreference(CategoryPreferences.BASTION_TYPE)
            .addPreference(CategoryPreferences.RANKED_LOOT_TABLE)
            .addPreference(CategoryPreferences.SPAWN_LOCATION)
            .addPreference(CategoryPreferences.ZOMBIE_PIGMEN)
            .register();
    public static final PracticeCategory FORTRESS_SPLIT = new PracticeCategory()
            .setId("fortress_split")
            .setFillerCategory(true)
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.FORTRESS)
                            .setChunkPos((category, random, world) -> new ChunkPos(0, 0))
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                            .setGeneratable(false)
            )
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos((category, random, world) -> CustomPortalForcer.getPortalPosition(new BlockPos(random.nextInt(5, 15) * (random.nextBoolean() ? -1 : 1), 64, random.nextInt(5, 15) * (random.nextBoolean() ? -1 : 1)), world))
                            .addPotionEffect(MiscPresets.FIRE_RESISTANCE_EFFECT)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.NETHER)
                            .setSpawnChunksDisabled(true)
                            .addProBiome(new WorldProperties.BiomeModification()
                                                 .setBiome(Biomes.SOUL_SAND_VALLEY)
                                                 .setRange(new WorldProperties.Range()
                                                                   .setRange(null)
                                                                   .addValidDimension(World.NETHER)
                                                                   .setCondition(CategoryPreferences.SOUL_SAND_VALLEY::getBoolValue)
                                                 )
                            )
            )
            .setSplitEvent(
                    new ChangeDimensionSplitEvent()
                            .setFromDimension(World.NETHER)
                            .setToDimension(World.OVERWORLD)
            )
            .addPreference(CategoryPreferences.GOOD_BLAZE_RATES)
            .addPreference(CategoryPreferences.SOUL_SAND_VALLEY)
            .addPreference(CategoryPreferences.FIRE_RESISTANCE)
            .register();
    public static final PracticeCategory NETHER_SPLIT = new PracticeCategory()
            .setId("nether_split")
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                            .setChunkPos((category, random, world) -> new ChunkPos(random.nextInt(6) + 2, random.nextInt(5) + 2))
                            .setRotation(BlockRotation.NONE)
                            .setGeneratable(false)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.FORTRESS)
                            .setChunkPos((category, random, world) -> {
                                StructureProperties properties = category.findStructureProperties(StructureFeature.BASTION_REMNANT);
                                if (properties.getChunkPos() != null) {
                                    ChunkPos pos = properties.getChunkPos();
                                    int offset = 6;
                                    int bound = 4;
                                    return new ChunkPos(
                                            pos.x + (random.nextBoolean() ? offset + random.nextInt(bound) : -(offset + random.nextInt(bound))),
                                            pos.z + (random.nextBoolean() ? offset + random.nextInt(bound) : -(offset + random.nextInt(bound)))
                                    );
                                }
                                return new ChunkPos(0, 0);
                            })
                            .setGeneratable(false)
            )
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos(TaskPresets.BASTION_SPAWN_POS)
                            .setSpawnAngle(TaskPresets.BASTION_SPAWN_ANGLE)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.NETHER)
                            .setSpawnChunksDisabled(true)
                            .addAntiBiome(new WorldProperties.BiomeModification()
                                                  .setBiome(Biomes.BASALT_DELTAS)
                                                  .setReplacement(Biomes.NETHER_WASTES)
                                                  .setRange(new WorldProperties.Range()
                                                                    .setRange(null)
                                                                    .addValidDimension(World.NETHER)
                                                  )
                            )
                            .addProBiome(new WorldProperties.BiomeModification()
                                                 .setBiome(Biomes.SOUL_SAND_VALLEY)
                                                 .setRange(new WorldProperties.Range()
                                                                   .setRange(null)
                                                                   .addValidDimension(World.NETHER)
                                                                   .setCondition(CategoryPreferences.SOUL_SAND_VALLEY::getBoolValue)
                                                 )
                            )
            )
            .setSplitEvent(
                    new ChangeDimensionSplitEvent()
                            .setFromDimension(World.NETHER)
                            .setToDimension(World.OVERWORLD)
            )
            .addPreference(CategoryPreferences.BASTION_TYPE)
            .addPreference(CategoryPreferences.RANKED_LOOT_TABLE)
            .addPreference(CategoryPreferences.GOOD_BLAZE_RATES)
            .addPreference(CategoryPreferences.SPAWN_LOCATION)
            .addPreference(CategoryPreferences.SOUL_SAND_VALLEY)
            .addPreference(CategoryPreferences.ZOMBIE_PIGMEN)
            .register();
    public static final PracticeCategory POST_BLIND_SPLIT = new PracticeCategory()
            .setId("post_blind_split")
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos((category, random, world) -> {
                                PreferenceTypes.StrongholdDistanceType distanceType = CategoryPreferences.STRONGHOLD_DISTANCE.getValue(category);
                                boolean isNetherSpawn = CategoryPreferences.POST_BLIND_SPAWN_DIMENSION.getValue().equals(PreferenceTypes.PostBlindSpawnDimensionType.NETHER);
                                ServerWorld overworld = world.getServer().getWorld(World.OVERWORLD);
                                if (overworld == null) {
                                    throw new NotInitializedException();
                                }
                                ChunkGenerator chunkGenerator = overworld.getChunkManager().getChunkGenerator();
                                chunkGenerator.generateStrongholdPositions();
                                List<ChunkPos> strongholds = chunkGenerator.strongholdPositions;
                                int maxStrongholds = 3;
                                BlockPos blockPos = strongholds.get(random.nextInt(maxStrongholds)).toBlockPos(4, 0, 4);
                                blockPos = PracticeCategoryUtils.getRandomBlockInRadius(distanceType.getMax(), distanceType.getMin(), blockPos, random);
                                if (!isNetherSpawn) {
                                    blockPos = new BlockPos(blockPos.getX(), PracticeCategoryUtils.findTopPos(world, blockPos), blockPos.getZ());
                                } else {
                                    blockPos = new BlockPos(blockPos.getX() / 8, blockPos.getY(), blockPos.getZ() / 8);
                                }
                                blockPos = CustomPortalForcer.createPortal(blockPos, world).down(2);
                                return blockPos;
                            })
                            .addPotionEffect(MiscPresets.FIRE_RESISTANCE_EFFECT)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey((category, random, world) -> CategoryPreferences.POST_BLIND_SPAWN_DIMENSION.getValue().key)
                            .setSpawnChunksDisabled(true)
            )
            .setSplitEvent(
                    new GetAdvancementSplitEvent()
                            .setAdvancement(new Identifier("story/follow_ender_eye"))
            )
            .addPreference(CategoryPreferences.STRONGHOLD_DISTANCE)
            .addPreference(CategoryPreferences.EYE_BREAKS)
            .addPreference(CategoryPreferences.FIRE_RESISTANCE)
            .addPreference(CategoryPreferences.POST_BLIND_SPAWN_DIMENSION)
            .register();
    public static final PracticeCategory STRONGHOLD_SPLIT = new PracticeCategory()
            .setId("stronghold_split")
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.STRONGHOLD)
                            .setChunkPos(new ChunkPos(0, 0))
                            .setOrientation(Direction.SOUTH)
                            .setGeneratable(true)
                            .setStructureTopY((category, random, world) -> random.nextInt(40, 55))
            )
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnPos((category, random, world) -> {
                                StructureProperties properties = category.findStructureProperties(DefaultBiomeFeatures.STRONGHOLD);
                                if (properties != null) {
                                    if (properties.hasStructureTopY()) {
                                        BlockPos pos = new BlockPos(4, properties.getStructureTopY() - 9, 4);
                                        BlockState state = world.getBlockState(pos.add(0, -1, 0));
                                        if (state.isOf(Blocks.AIR) || state.isOf(Blocks.CAVE_AIR)) {
                                            return pos.add(0, 6, -1);
                                        }
                                        return pos;
                                    }
                                }
                                throw new NotInitializedException();
                            })
                            .setSpawnAngle(0.0F, 0.0F)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.OVERWORLD)
                            .setSpawnChunksDisabled(true)
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.NORMAL_MINESHAFT)
                            .setGeneratable((category, random, world) -> !CategoryPreferences.DISABLE_MINESHAFTS.getBoolValue(category))
            )
            .addStructureProperties(
                    new StructureProperties()
                            .setStructure(DefaultBiomeFeatures.MESA_MINESHAFT)
                            .setGeneratable((category, random, world) -> !CategoryPreferences.DISABLE_MINESHAFTS.getBoolValue(category))
            )
            .setSplitEvent(
                    new ChangeDimensionSplitEvent()
                            .setFromDimension(World.OVERWORLD)
                            .setToDimension(World.END)
            )
            .addPreference(CategoryPreferences.DISABLE_MINESHAFTS)
            .addPreference(CategoryPreferences.DISABLE_DUNGEONS)
            .addPreference(CategoryPreferences.EYE_COUNT)
            .register();
    public static final PracticeCategory END_SPLIT = new PracticeCategory()
            .setId("end_split")
            .setPlayerProperties(
                    new PlayerProperties()
                            .setSpawnAngle(90.0F, 0.0F)
                            .setSpawnPos(ServerWorld.END_SPAWN_POS)
            )
            .setWorldProperties(
                    new WorldProperties()
                            .setWorldRegistryKey(World.END)
            )
            .setSplitEvent(
                    new ChangeDimensionSplitEvent()
                            .setFromDimension(World.END)
                            .setToDimension(World.OVERWORLD)
            )
            .addPreference(CategoryPreferences.NO_EARLY_FLYAWAY)
            .addPreference(CategoryPreferences.NO_CAGE_SPAWN)
            .addPreference(CategoryPreferences.START_NODE)
            .addPreference(CategoryPreferences.ONE_IN_EIGHT)
            .addPreference(CategoryPreferences.TOWER_HEIGHT)
            .register();
}
