package me.falu.peepopractice.core.category;

import com.google.common.collect.Lists;
import me.falu.peepopractice.core.CustomPortalForcer;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.category.properties.event.*;
import me.falu.peepopractice.core.category.properties.preset.BastionPreset;
import me.falu.peepopractice.core.category.properties.preset.FortressPreset;
import me.falu.peepopractice.core.category.properties.preset.StructurePreset;
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
            .setPlayerProperties(new PlayerProperties()
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
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.BEACHED_SHIPWRECK)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.SUNKEN_SHIPWRECK)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.COLD_OCEAN_RUIN)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.WARM_OCEAN_RUIN)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.NORMAL_MINESHAFT)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.DESERT_VILLAGE)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.PLAINS_VILLAGE)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.SAVANNA_VILLAGE)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.TAIGA_VILLAGE)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.SNOWY_VILLAGE)
                                            .setGeneratable(false)
            )
            .setWorldProperties(new WorldProperties()
                                        .setWorldRegistryKey(World.OVERWORLD)
            )
            .setSplitEvent(new InteractLootChestSplitEvent()
                                   .setLootTable(LootTables.BURIED_TREASURE_CHEST)
                                   .setOnClose(true)
            );
    public static final PracticeCategory ISLAND_LEAVE_SPLIT = new PracticeCategory()
            .setId("island_leave_split")
            .setPlayerProperties(new PlayerProperties()
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
            ).setWorldProperties(new WorldProperties()
                                         .setWorldRegistryKey(World.OVERWORLD)
            ).setSplitEvent(new EnterVehicleSplitEvent()
                                    .setVehicle(EntityType.BOAT)
                                    .setKeepItem(true)
            );
    public static final PracticeCategory RAVINE_ENTER_SPLIT = new PracticeCategory()
            .setId("ravine_enter_split")
            .setPlayerProperties(new PlayerProperties()
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
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.MONUMENT)
                                            .setGeneratable(false)
            )
            .setWorldProperties(new WorldProperties()
                                        .setWorldRegistryKey(World.OVERWORLD)
                                        .addProBiome(new WorldProperties.BiomeModification()
                                                             .setBiome(Biomes.DEEP_OCEAN)
                                                             .setRange(new WorldProperties.Range()
                                                                               .setRange(null)
                                                                               .addValidDimension(World.OVERWORLD)
                                                             )
                                        )
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                                   .setToDimension(World.NETHER)
            )
            .addPreference(new CategoryPreference()
                                   .setId("fix_ghost_buckets")
                                   .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                                   .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                                   .setIcon(new Identifier("textures/item/water_bucket.png"))
            );
    public static final PracticeCategory BASTION_SPLIT = new PracticeCategory()
            .setId("bastion_split")
            .setFillerCategory(true)
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                                            .setChunkPos((category, random, world) -> {
                                                PracticeTypes.SpawnLocationType spawnLocation = PracticeTypes.SpawnLocationType.fromLabel(CategoryPreference.getValue(category, "spawn_location"));
                                                if (spawnLocation != null && spawnLocation.equals(PracticeTypes.SpawnLocationType.TERRAIN)) {
                                                    int mx = random.nextBoolean() ? 1 : -1;
                                                    int mz = random.nextBoolean() ? 1 : -1;
                                                    return new ChunkPos(random.nextInt(3, 4) * mx, random.nextInt(3, 4) * mz);
                                                }
                                                return new ChunkPos(0, 0);
                                            })
                                            .setRotation(BlockRotation.NONE)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.FORTRESS)
                                            .setGeneratable(false)
            )
            .setPlayerProperties(new PlayerProperties()
                                         .setSpawnPos(BastionPreset.BASTION_SPAWN_POS)
                                         .setSpawnAngle(BastionPreset.BASTION_SPAWN_ANGLE)
            )
            .setWorldProperties(new WorldProperties()
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
            .setSplitEvent(new ThrowEntitySplitEvent()
                                   .setItem(Items.ENDER_PEARL)
            )
            .addPreference(BastionPreset.BASTION_TYPE_PREFERENCE)
            .addPreference(BastionPreset.RANKED_LOOT_TABLE_PREFERENCE)
            .addPreference(StructurePreset.SPAWN_LOCATION_PREFERENCE)
            .addPreference(BastionPreset.ZOMBIE_PIGMEN);
    public static final PracticeCategory FORTRESS_SPLIT = new PracticeCategory()
            .setId("fortress_split")
            .setFillerCategory(true)
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.FORTRESS)
                                            .setChunkPos((category, random, world) -> new ChunkPos(0, 0))
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                                            .setGeneratable(false)
            )
            .setPlayerProperties(new PlayerProperties()
                                         .setSpawnPos((category, random, world) -> CustomPortalForcer.getPortalPosition(new BlockPos(random.nextInt(5, 15) * (random.nextBoolean() ? -1 : 1), 64, random.nextInt(5, 15) * (random.nextBoolean() ? -1 : 1)), world))
                                         .addPotionEffect(FortressPreset.FIRE_RESISTANCE_EFFECT)
            )
            .setWorldProperties(new WorldProperties()
                                        .setWorldRegistryKey(World.NETHER)
                                        .setSpawnChunksDisabled(true)
                                        .addProBiome(new WorldProperties.BiomeModification()
                                                             .setBiome(Biomes.SOUL_SAND_VALLEY)
                                                             .setRange(new WorldProperties.Range()
                                                                               .setRange(null)
                                                                               .addValidDimension(World.NETHER)
                                                                               .setCondition(() -> CategoryPreference.getBoolValue("ssv"))
                                                             )
                                        )
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                                   .setToDimension(World.OVERWORLD)
            )
            .addPreference(FortressPreset.GOOD_BLAZE_RATES_PREFERENCE)
            .addPreference(FortressPreset.SOUL_SAND_VALLEY_PREFERENCE)
            .addPreference(FortressPreset.FIRE_RESISTANCE_PREFERENCE);
    public static final PracticeCategory NETHER_SPLIT = new PracticeCategory()
            .setId("nether_split")
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                                            .setChunkPos((category, random, world) -> new ChunkPos(random.nextInt(6) + 2, random.nextInt(5) + 2))
                                            .setRotation(BlockRotation.NONE)
                                            .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
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
            .setPlayerProperties(new PlayerProperties()
                                         .setSpawnPos(BastionPreset.BASTION_SPAWN_POS)
                                         .setSpawnAngle(BastionPreset.BASTION_SPAWN_ANGLE)
            )
            .setWorldProperties(new WorldProperties()
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
                                                                               .setCondition(() -> CategoryPreference.getBoolValue("ssv"))
                                                             )
                                        )
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                                   .setToDimension(World.OVERWORLD)
            )
            .addPreference(BastionPreset.BASTION_TYPE_PREFERENCE)
            .addPreference(BastionPreset.RANKED_LOOT_TABLE_PREFERENCE)
            .addPreference(FortressPreset.GOOD_BLAZE_RATES_PREFERENCE)
            .addPreference(StructurePreset.SPAWN_LOCATION_PREFERENCE)
            .addPreference(FortressPreset.SOUL_SAND_VALLEY_PREFERENCE)
            .addPreference(BastionPreset.ZOMBIE_PIGMEN);
    public static final PracticeCategory POST_BLIND_SPLIT = new PracticeCategory()
            .setId("post_blind_split")
            .setPlayerProperties(new PlayerProperties()
                                         .setSpawnPos((category, random, world) -> {
                                             PracticeTypes.StrongholdDistanceType distanceType = PracticeTypes.StrongholdDistanceType.fromLabel(CategoryPreference.getValue(category, "stronghold_distance"));
                                             distanceType = distanceType == null ? PracticeTypes.StrongholdDistanceType.AVERAGE : distanceType;
                                             ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
                                             chunkGenerator.method_28509();
                                             List<ChunkPos> strongholds = chunkGenerator.field_24749;
                                             int maxStrongholds = 3;
                                             BlockPos blockPos = strongholds.get(random.nextInt(maxStrongholds)).toBlockPos(4, 0, 4);
                                             blockPos = PracticeCategoryUtils.getRandomBlockInRadius(distanceType.getMax(), distanceType.getMin(), blockPos, random);
                                             blockPos = new BlockPos(blockPos.getX(), PracticeCategoryUtils.findTopPos(world, blockPos), blockPos.getZ());
                                             blockPos = CustomPortalForcer.createPortal(blockPos, world).down(2);
                                             return blockPos;
                                         })
                                         .addPotionEffect(FortressPreset.FIRE_RESISTANCE_EFFECT)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.FORTRESS)
                                            .setChunkPos((category, random, world) -> {
                                                if (category.hasPlayerProperties()) {
                                                    PlayerProperties properties = category.getPlayerProperties();
                                                    if (properties.hasSpawnPos()) {
                                                        BlockPos pos = properties.getSpawnPos();
                                                        BlockPos netherPos = new BlockPos(pos.getX() / 8, pos.getY(), pos.getZ() / 8);
                                                        netherPos = PracticeCategoryUtils.getRandomBlockInRadius(20, netherPos, random);
                                                        return new ChunkPos(netherPos);
                                                    }
                                                }
                                                throw new NotInitializedException();
                                            })
                                            .setGeneratable(false)
            )
            .setWorldProperties(new WorldProperties()
                                        .setWorldRegistryKey(World.OVERWORLD)
                                        .setSpawnChunksDisabled(true)
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                                   .setAdvancement(new Identifier("story/follow_ender_eye"))
            )
            .addPreference(new CategoryPreference()
                                   .setId("stronghold_distance")
                                   .setIcon(new Identifier("textures/mob_effect/speed.png"))
                                   .setChoices(PracticeTypes.StrongholdDistanceType.all())
                                   .setDefaultChoice(PracticeTypes.StrongholdDistanceType.AVERAGE.getLabel())
            )
            .addPreference(new CategoryPreference()
                                   .setId("eye_breaks")
                                   .setIcon(new Identifier("textures/item/ender_eye.png"))
                                   .setChoices(PracticeCategoryUtils.ALL_LIST)
                                   .setDefaultChoice(PracticeCategoryUtils.RANDOM)
            )
            .addPreference(FortressPreset.FIRE_RESISTANCE_PREFERENCE);
    public static final PracticeCategory STRONGHOLD_SPLIT = new PracticeCategory()
            .setId("stronghold_split")
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.STRONGHOLD)
                                            .setChunkPos(new ChunkPos(0, 0))
                                            .setOrientation(Direction.SOUTH)
                                            .setGeneratable(true)
                                            .setStructureTopY((category, random, world) -> random.nextInt(40, 55))
            )
            .setPlayerProperties(new PlayerProperties()
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
            .setWorldProperties(new WorldProperties()
                                        .setWorldRegistryKey(World.OVERWORLD)
                                        .setSpawnChunksDisabled(true)
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.NORMAL_MINESHAFT)
                                            .setGeneratable((category, random, world) -> !CategoryPreference.getBoolValue(category, "disable_mineshafts"))
            )
            .addStructureProperties(new StructureProperties()
                                            .setStructure(DefaultBiomeFeatures.MESA_MINESHAFT)
                                            .setGeneratable((category, random, world) -> !CategoryPreference.getBoolValue(category, "disable_mineshafts"))
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                                   .setToDimension(World.END)
            )
            .addPreference(new CategoryPreference()
                                   .setId("disable_mineshafts")
                                   .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                                   .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                                   .setIcon(new Identifier("textures/item/string.png"))
            )
            .addPreference(new CategoryPreference()
                                   .setId("disable_dungeons")
                                   .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                                   .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                                   .setIcon(new Identifier("textures/item/spawn_egg.png"))
            )
            .addPreference(new CategoryPreference()
                                   .setId("eye_count")
                                   .setChoices(PracticeTypes.EyeCountType.all())
                                   .setDefaultChoice(PracticeTypes.EyeCountType.RANDOM.getLabel())
                                   .setIcon(new Identifier("textures/item/ender_eye.png"))
            );
    public static final PracticeCategory END_SPLIT = new PracticeCategory()
            .setId("end_split")
            .setPlayerProperties(new PlayerProperties()
                                         .setSpawnAngle(90.0F, 0.0F)
                                         .setSpawnPos(ServerWorld.END_SPAWN_POS)
            )
            .setWorldProperties(new WorldProperties()
                                        .setWorldRegistryKey(World.END)
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                                   .setToDimension(World.OVERWORLD)
            )
            .addPreference(new CategoryPreference()
                                   .setId("no_early_flyaway")
                                   .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                                   .setDefaultChoice(PracticeCategoryUtils.ENABLED)
                                   .setIcon(new Identifier("textures/mob_effect/levitation.png"))
            )
            .addPreference(new CategoryPreference()
                                   .setId("no_cage_spawn")
                                   .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                                   .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                                   .setIcon(new Identifier("textures/mob_effect/jump_boost.png"))
            )
            .addPreference(new CategoryPreference()
                                   .setId("start_node")
                                   .setChoices(PracticeTypes.StartNodeType.all())
                                   .setDefaultChoice(PracticeTypes.StartNodeType.RANDOM.getLabel())
                                   .setIcon(new Identifier("textures/block/obsidian.png"))
            )
            .addPreference(new CategoryPreference()
                                   .setId("one_in_eight")
                                   .setChoices(PracticeCategoryUtils.ALL_LIST)
                                   .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                                   .setIcon(new Identifier("textures/item/brick.png"))
            );
}
