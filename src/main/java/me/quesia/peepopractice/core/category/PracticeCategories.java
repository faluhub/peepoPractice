package me.quesia.peepopractice.core.category;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import me.quesia.peepopractice.core.CustomPortalForcer;
import me.quesia.peepopractice.core.category.properties.PlayerProperties;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import me.quesia.peepopractice.core.category.properties.WorldProperties;
import me.quesia.peepopractice.core.category.properties.event.ChangeDimensionSplitEvent;
import me.quesia.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import me.quesia.peepopractice.core.category.properties.event.ThrowEntitySplitEvent;
import me.quesia.peepopractice.core.category.properties.preset.BastionPreset;
import me.quesia.peepopractice.mixin.access.ChunkGeneratorAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class PracticeCategories {
    public static List<PracticeCategory> ALL = new ArrayList<>();
    public static PracticeCategory EMPTY = new PracticeCategory()
            .setId("empty")
            .setHidden(true);
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
                            boolean bl = random.nextBoolean();
                            return new ChunkPos(
                                    pos.x + (bl ? offset + random.nextInt(bound) : -(offset + random.nextInt(offset))),
                                    pos.z + (!bl ? offset + random.nextInt(bound) : -(offset + random.nextInt(offset)))
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
                                pos = pos.add(0, -pos.getY() + 66, 0);
                                break;
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
    public static PracticeCategory POST_BLIND = new PracticeCategory()
            .setId("post_blind")
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos((category, random, world) -> {
                        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
                        ((ChunkGeneratorAccessor) chunkGenerator).invokeMethod_28509();
                        List<ChunkPos> strongholds = ((ChunkGeneratorAccessor) chunkGenerator).getField_24749();
                        BlockPos blockPos = strongholds.get(random.nextInt(strongholds.size() - 1)).toBlockPos(4, 0, 4);
                        int radius = 1600;
                        double ang = random.nextDouble() * 2 * Math.PI;
                        double hyp = Math.sqrt(random.nextDouble()) * radius;
                        double adj = Math.cos(ang) * hyp;
                        double opp = Math.sin(ang) * hyp;
                        blockPos = new BlockPos(blockPos.getX() + adj, 0, blockPos.getZ() + opp);
                        blockPos = new BlockPos(blockPos.getX(), PracticeCategoryUtils.findTopPos(world, blockPos), blockPos.getZ());
                        blockPos = CustomPortalForcer.createPortal(blockPos, world).down();
                        return blockPos;
                    })
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(new Identifier("story/follow_ender_eye"))
            );
    public static PracticeCategory STRONGHOLD = new PracticeCategory()
            .setId("stronghold")
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.STRONGHOLD)
                    .setChunkPos(new ChunkPos(0, 0))
                    .setOrientation(Direction.SOUTH)
                    .setGeneratable(true)
                    .setStructureTopY(50)
            )
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos(new BlockPos(4, 41, 4))
                    .setSpawnAngle(0.0F, 0.0F)
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
            )
            .setSplitEvent(new ChangeDimensionSplitEvent()
                    .setDimension(World.END)
            )
            .addPreference(new CategoryPreference()
                    .setId("spawn_in_stronghold")
                    .setLabel("Spawn in Stronghold")
                    .setDescription("If enabled, you'll spawn inside of the Stronghold.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.ENABLED)
                    .setIcon(new Identifier("textures/item/iron_shovel.png"))
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
                    .addChoice("Random")
                    .addChoice("0")
                    .addChoice("12")
                    .setDefaultChoice("Random")
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
