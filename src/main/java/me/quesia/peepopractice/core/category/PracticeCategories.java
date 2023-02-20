package me.quesia.peepopractice.core.category;

import com.google.common.collect.Lists;
import me.quesia.peepopractice.core.CustomPortalForcer;
import me.quesia.peepopractice.core.NotInitializedException;
import me.quesia.peepopractice.core.category.properties.PlayerProperties;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import me.quesia.peepopractice.core.category.properties.WorldProperties;
import me.quesia.peepopractice.core.category.properties.event.ChangeDimensionSplitEvent;
import me.quesia.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import me.quesia.peepopractice.core.category.properties.event.InteractLootableContainerSplitEvent;
import me.quesia.peepopractice.core.category.properties.event.ThrowEntitySplitEvent;
import me.quesia.peepopractice.core.category.properties.preset.BastionPreset;
import me.quesia.peepopractice.mixin.access.ChunkGeneratorAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnusedDeclaration")
public class PracticeCategories {
    public static List<PracticeCategory> ALL = new ArrayList<>();
    public static PracticeCategory EMPTY = new PracticeCategory()
            .setId("empty")
            .setHidden(true);
    public static PracticeCategory MAPLESS_SPLIT = new PracticeCategory()
            .setId("mapless_split")
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos((category, random, world) -> {
                        BlockPos spawnPos = null;
                        BlockPos btPos = null;
                        do {
                            if (btPos == null) {
                                btPos = new BlockPos(0, 0, 0);
                            }
                            btPos = world.locateStructure(StructureFeature.BURIED_TREASURE, new BlockPos(-btPos.getX(), 0, -btPos.getZ()), 100, false);
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
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
            )
            .setSplitEvent(new InteractLootableContainerSplitEvent()
                    .setBlockEntityType(BlockEntityType.CHEST)
                    .setLootTable(LootTables.BURIED_TREASURE_CHEST)
            );
    public static PracticeCategory RAVINE_ENTER_SPLIT = new PracticeCategory()
            .setId("ravine_enter_split")
            .setPlayerProperties(new PlayerProperties()
                    .setVehicle(EntityType.BOAT)
                    .setSpawnPos((category, random, world) -> {
                        if (category.hasCustomValue("ravinePosition")) {
                            BlockPos ravinePos = (BlockPos) category.getCustomValue("ravinePosition");
                            BlockPos pos = new BlockPos(ravinePos.getX(), world.getChunkManager().getChunkGenerator().getSeaLevel(), ravinePos.getZ());
                            return PracticeCategoryUtils.getRandomBlockInRadius(20, pos, random);
                        }
                        throw new NotInitializedException();
                    })
                    .setSpawnAngle((category, random, world) -> {
                        if (category.hasPlayerProperties() && category.getPlayerProperties().getSpawnPos() != null) {
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
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .addProBiomeRange(Biomes.DEEP_OCEAN, null)
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                    .setDimension(World.NETHER)
            );
    public static PracticeCategory BASTION_SPLIT = new PracticeCategory()
            .setId("bastion_split")
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                    .setChunkPos(new ChunkPos(0, 0))
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
                    .addAntiBiomeRange(Biomes.BASALT_DELTAS, null)
            )
            .setSplitEvent(new ThrowEntitySplitEvent()
                    .setItem(Items.ENDER_PEARL)
            )
            .addPreference(new CategoryPreference()
                    .setId("bastion_type")
                    .setLabel("Bastion Type")
                    .setDescription("The bastion type.")
                    .setIcon(new Identifier("textures/item/golden_helmet.png"))
                    .setChoices(PracticeTypes.BastionType.all())
                    .setDefaultChoice(PracticeTypes.BastionType.RANDOM.getLabel())
            );
    public static PracticeCategory FORTRESS_SPLIT = new PracticeCategory()
            .setId("fortress_split")
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.FORTRESS)
                    .setChunkPos(new ChunkPos(0, 0))
                    .setGeneratable(false)
            )
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                    .setGeneratable(false)
            )
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos((category, random, world) -> {
                        BlockPos pos = new BlockPos(11, 127, 11);
                        while (!world.getBlockState(pos).isOf(Blocks.NETHER_BRICKS)) {
                            pos = pos.add(0, -1, 0);
                            if (pos.getY() < 0) {
                                throw new NotInitializedException();
                            }
                        }
                        return pos.add(0, 1, 0);
                    })
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.NETHER)
                    .setSpawnChunksDisabled(true)
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                    .setDimension(World.OVERWORLD)
            );
    public static PracticeCategory NETHER_SPLIT = new PracticeCategory()
            .setId("nether_split")
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                    .setChunkPos((category, random, world) -> new ChunkPos(random.nextInt(3) + 2, random.nextInt(3) + 2))
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
                                    pos.x + (random.nextBoolean() ? offset + random.nextInt(bound) : -(offset + random.nextInt(offset))),
                                    pos.z + (random.nextBoolean() ? offset + random.nextInt(bound) : -(offset + random.nextInt(offset)))
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
                    .addAntiBiomeRange(Biomes.BASALT_DELTAS, null)
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                    .setDimension(World.OVERWORLD)
            )
            .addPreference(new CategoryPreference()
                    .setId("bastion_type")
                    .setLabel("Bastion Type")
                    .setDescription("The bastion type.")
                    .setIcon(new Identifier("textures/item/golden_helmet.png"))
                    .setChoices(PracticeTypes.BastionType.all())
                    .setDefaultChoice(PracticeTypes.BastionType.RANDOM.getLabel())
            );
    public static PracticeCategory POST_BLIND_SPLIT = new PracticeCategory()
            .setId("post_blind_split")
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos((category, random, world) -> {
                        PracticeTypes.StrongholdDistanceType distanceType = PracticeTypes.StrongholdDistanceType.fromLabel(CategoryPreference.getValue(category, "stronghold_distance", PracticeTypes.StrongholdDistanceType.AVERAGE.getLabel()));
                        int max = Objects.requireNonNullElse(distanceType, PracticeTypes.StrongholdDistanceType.AVERAGE).getMax();
                        int min = Objects.requireNonNullElse(distanceType, PracticeTypes.StrongholdDistanceType.AVERAGE).getMin();
                        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
                        ((ChunkGeneratorAccessor) chunkGenerator).invokeMethod_28509();
                        List<ChunkPos> strongholds = ((ChunkGeneratorAccessor) chunkGenerator).getField_24749();
                        int maxStrongholds = 9;
                        BlockPos blockPos = strongholds.get(random.nextInt(maxStrongholds)).toBlockPos(4, 0, 4);
                        blockPos = PracticeCategoryUtils.getRandomBlockInRadius(max, min, blockPos, random);
                        blockPos = new BlockPos(blockPos.getX(), PracticeCategoryUtils.findTopPos(world, blockPos), blockPos.getZ());
                        blockPos = CustomPortalForcer.createPortal(blockPos, world).down(2);
                        return blockPos;
                    })
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
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(new Identifier("story/follow_ender_eye"))
            )
            .addPreference(new CategoryPreference()
                    .setId("stronghold_distance")
                    .setLabel("Stronghold Distance")
                    .setDescription("Close (200-500), Average (700-1000), Far (1200-1600) or Random (200-1600).")
                    .setIcon(new Identifier("textures/mob_effect/speed.png"))
                    .setChoices(PracticeTypes.StrongholdDistanceType.all())
                    .setDefaultChoice(PracticeTypes.StrongholdDistanceType.AVERAGE.getLabel())
            );
    public static PracticeCategory STRONGHOLD_SPLIT = new PracticeCategory()
            .setId("stronghold_split")
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.STRONGHOLD)
                    .setChunkPos(new ChunkPos(0, 0))
                    .setOrientation(Direction.SOUTH)
                    .setGeneratable(true)
                    .setStructureTopY(40)
            )
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos(new BlockPos(4, 31, 4))
                    .setSpawnAngle(0.0F, 0.0F)
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
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
                    .setDimension(World.END)
            )
            .addPreference(new CategoryPreference()
                    .setId("disable_mineshafts")
                    .setLabel("Disable Mineshafts")
                    .setDescription("If enabled, Mineshafts won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                    .setIcon(new Identifier("textures/item/string.png"))
            )
            .addPreference(new CategoryPreference()
                    .setId("disable_dungeons")
                    .setLabel("Disable Dungeons")
                    .setDescription("If enabled, Dungeons won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
                    .setIcon(new Identifier("textures/item/spawn_egg.png"))
            )
            .addPreference(new CategoryPreference()
                    .setId("eye_count")
                    .setLabel("Eye Count")
                    .setDescription("Specify the amount of eyes that are filled in.")
                    .setChoices(PracticeTypes.EyeCountType.all())
                    .setDefaultChoice(PracticeTypes.EyeCountType.RANDOM.getLabel())
                    .setIcon(new Identifier("textures/item/ender_eye.png"))
            );
    public static PracticeCategory END_SPLIT = new PracticeCategory()
            .setId("end_split")
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnAngle(90.0F, 0.0F)
                    .setSpawnPos(ServerWorld.END_SPAWN_POS)
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.END)
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                    .setDimension(World.OVERWORLD)
            );
}
