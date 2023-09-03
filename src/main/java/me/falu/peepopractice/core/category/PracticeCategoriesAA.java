package me.falu.peepopractice.core.category;

import me.falu.peepopractice.core.CustomPortalForcer;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import me.falu.peepopractice.core.category.properties.preset.CommandsPreset;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class PracticeCategoriesAA {
    public static final List<PracticeCategory> ALL = new ArrayList<>();

    public static final PracticeCategory UNEASY_ALLIANCE_SPLIT = new PracticeCategory(true)
            .setId("uneasy_alliance_split")
            .setPlayerProperties(new PlayerProperties()
                    .setSpawnPos((category, random, world) -> CustomPortalForcer.getPortalPosition(new BlockPos(random.nextInt(50, 250) * (random.nextBoolean() ? -1 : 1), 64, random.nextInt(50, 250) * (random.nextBoolean() ? -1 : 1)), world))
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.NETHER)
                    .setSpawnChunksDisabled(true)
                    .addProBiomeRange(Biomes.SOUL_SAND_VALLEY, null)
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(new Identifier("nether/uneasy_alliance"))
            );
    public static final PracticeCategory IGLOO_SPLIT = new PracticeCategory(true)
            .setId("igloo_split")
            .putCustomValue("guaranteeIglooBasement", true)
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .setSpawnChunksDisabled(true)
                    .addProBiomeRange(Biomes.SNOWY_TUNDRA, 400)
            )
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.IGLOO)
                    .setChunkPos(new ChunkPos(0, 0))
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(new Identifier("story/cure_zombie_villager.json"))
            );
    public static final PracticeCategory RAID_SPLIT = new PracticeCategory(true)
            .setId("raid_split")
            .setFillerCategory(true)
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .setSeedList("raid")
            );
    public static final PracticeCategory END_GAME_SPLIT = new PracticeCategory(true)
            .setId("end_game_split")
            .setPlayerProperties(new PlayerProperties()
                    .runCommands(new String[]{
                            "execute at @a run summon minecraft:boat ~1 ~3 ~ {Passengers:[{id:\"minecraft:shulker\",Color:16}]}"
                    })
                    .runCommands(CommandsPreset.ADVANCEMENT_GRANT_END_GAME)
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .setSeedList("end_game")
                    .setDragonKilled(true)
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(null)
            );
}
