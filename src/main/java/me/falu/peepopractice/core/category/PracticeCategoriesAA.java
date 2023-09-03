package me.falu.peepopractice.core.category;

import me.falu.peepopractice.core.CustomPortalForcer;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import me.falu.peepopractice.core.category.properties.preset.CommandsPreset;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

import java.util.ArrayList;
import java.util.List;

/**
 * - End game
 * - Uneasy Alliance
 * - Igloo
 * - Raid
 */
@SuppressWarnings("UnusedDeclaration")
public class PracticeCategoriesAA {
    public static final List<PracticeCategory> ALL = new ArrayList<>();

    public static final PracticeCategory UNEASY_ALLIANCE = new PracticeCategory(true)
            .setId("uneasy_alliance")
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
    public static final PracticeCategory END_GAME = new PracticeCategory(true)
            .setId("end_game")
            .setPlayerProperties(new PlayerProperties()
                    .runCommands(new String[] {
                            "execute at @a run summon minecraft:boat ~1 ~3 ~ {Passengers:[{id:\"minecraft:shulker\",Color:16}]}"
                    })
                    .runCommands(CommandsPreset.ADVANCEMENT_GRANT_END_GAME)
            )
            .setWorldProperties(new WorldProperties()
                    .setWorldRegistryKey(World.OVERWORLD)
                    .setSeedListPath("end_game")
                    .setDragonKilled(true)
            )
            .setSplitEvent(new GetAdvancementSplitEvent()
                    .setAdvancement(null)
            );
}
