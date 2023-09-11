package me.falu.peepopractice.core.category;

import me.falu.peepopractice.core.CustomPortalForcer;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
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
                    .addProBiome(new WorldProperties.BiomeModification()
                            .setBiome(Biomes.SOUL_SAND_VALLEY)
                            .setRange(new WorldProperties.Range()
                                    .setRange(null)
                                    .addValidDimension(World.NETHER)
                            )
                    )
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(new Identifier("nether/uneasy_alliance"))
            );
    public static final PracticeCategory IGLOO_SPLIT = new PracticeCategory(true)
            .setId("igloo_split")
            .putPermaValue("guaranteeIglooBasement", true)
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .setSpawnChunksDisabled(true)
                    .addProBiome(new WorldProperties.BiomeModification()
                            .setBiome(Biomes.SNOWY_TUNDRA)
                            .setRange(new WorldProperties.Range()
                                    .setRange(60)
                                    .addValidDimension(World.OVERWORLD)
                            )
                    )
                    .setStartDifficulty(Difficulty.HARD)
            )
            .addStructureProperties(new StructureProperties()
                    .setStructure(DefaultBiomeFeatures.IGLOO)
                    .setChunkPos(new ChunkPos(0, 0))
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(new Identifier("story/cure_zombie_villager"))
            );
    public static final PracticeCategory RAID_SPLIT = new PracticeCategory(true)
            .setId("raid_split")
            .setFillerCategory(true)
            .setPlayerProperties(new PlayerProperties()
                    .addPotionEffect(new PlayerProperties.PotionEffect()
                            .setEffect(StatusEffects.BAD_OMEN)
                    )
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .setSpawnChunksDisabled(true)
                    .setSeedList("raid")
                    .setStartDifficulty(Difficulty.NORMAL)
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(new Identifier("adventure/hero_of_the_village"))
            );
    public static final PracticeCategory END_GAME_SPLIT = new PracticeCategory(true)
            .setId("end_game_split")
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .setSeedList("end_game")
                    .useDatapack("aa_endgame")
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(null)
            );
}
