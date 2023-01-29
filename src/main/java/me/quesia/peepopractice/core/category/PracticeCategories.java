package me.quesia.peepopractice.core.category;

import me.quesia.peepopractice.core.category.properties.PlayerProperties;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import me.quesia.peepopractice.core.category.properties.WorldProperties;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class PracticeCategories {
    public static List<PracticeCategory> ALL = new ArrayList<>();
    public static PracticeCategory STRONGHOLD = new PracticeCategory()
            .setId("stronghold")
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.STRONGHOLD)
                    .setChunkPos(new ChunkPos(0, 0))
                    .setOrientation(Direction.SOUTH)
//                    .setUnique(true)
                    .setStructureTopY(50)
            )
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos(new BlockPos(4, 41, 4))
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
            )
            .addSetting(new CategorySetting()
                    .setId("spawn_in_stronghold")
                    .setLabel("Spawn in Stronghold")
                    .setDescription("If enabled, you'll spawn inside of the Stronghold.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.ENABLED)
            )
            .addSetting(new CategorySetting()
                    .setId("disable_mineshafts")
                    .setLabel("Disable Mineshafts")
                    .setDescription("If enabled, Mineshafts won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
            )
            .addSetting(new CategorySetting()
                    .setId("disable_dungeons")
                    .setLabel("Disable Dungeons")
                    .setDescription("If enabled, Dungeons won't generate.")
                    .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
                    .setDefaultChoice(PracticeCategoryUtils.DISABLED)
            )
            .addSetting(new CategorySetting()
                    .setId("eye_count")
                    .setLabel("Eye Count")
                    .setDescription("Specify the amount of eyes that are filled in.")
                    .addChoice("Random")
                    .addChoice("0")
                    .addChoice("12")
                    .setDefaultChoice("Random")
            );
    public static PracticeCategory OVERWORLD_TEST = new PracticeCategory()
            .setId("overworld_test")
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
            );
    public static PracticeCategory NETHER_TEST = new PracticeCategory()
            .setId("nether_test")
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.BASTION_REMNANT)
                    .setChunkPos(new ChunkPos(2, 2))
                    .setUnique(true)
            )
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.FORTRESS)
                    .setChunkPos(new ChunkPos(-2, -2))
                    .setUnique(true)
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.NETHER)
            );
    public static PracticeCategory END_TEST = new PracticeCategory()
            .setId("end_test")
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnAngle(90.0F, 0.0F)
                    .setSpawnPos(ServerWorld.END_SPAWN_POS)
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.END)
            );
}
