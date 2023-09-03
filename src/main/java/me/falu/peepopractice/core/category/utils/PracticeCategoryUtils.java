package me.falu.peepopractice.core.category.utils;

import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.PracticeCategory;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PracticeCategoryUtils {
    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    public static final String RANDOM = "Random";
    public static final String[] BOOLEAN_LIST = new String[] { ENABLED, DISABLED };
    public static final String[] ALL_LIST = new String[] { ENABLED, DISABLED, RANDOM };
    /**
     * List made by Quidvio. 💖
     */
    public static final String[] ADVANCEMENTS = {
            "adventure/adventuring_time",
            "adventure/arbalistic",
            "adventure/bullseye",
            "adventure/hero_of_the_village",
            "adventure/honey_block_slide",
            "adventure/kill_a_mob",
            "adventure/kill_all_mobs",
            "adventure/ol_betsy",
            "adventure/root",
            "adventure/shoot_arrow",
            "adventure/sleep_in_bed",
            "adventure/sniper_duel",
            "adventure/summon_iron_golem",
            "adventure/throw_trident",
            "adventure/totem_of_undying",
            "adventure/trade",
            "adventure/two_birds_one_arrow",
            "adventure/very_very_frightening",
            "adventure/voluntary_exile",
            "adventure/whos_the_pillager_now",

            "end/dragon_breath",
            "end/dragon_egg",
            "end/elytra",
            "end/enter_end_gateway",
            "end/find_end_city",
            "end/kill_dragon",
            "end/levitate",
            "end/respawn_dragon",
            "end/root",

            "husbandry/balanced_diet",
            "husbandry/bred_all_animals",
            "husbandry/breed_an_animal",
            "husbandry/complete_catalogue",
            "husbandry/fishy_business",
            "husbandry/obtain_netherite_hoe",
            "husbandry/plant_seed",
            "husbandry/root",
            "husbandry/safely_harvest_honey",
            "husbandry/silk_touch_nest",
            "husbandry/tactical_fishing",
            "husbandry/tame_an_animal",

            "nether/all_effects",
            "nether/all_potions",
            "nether/brew_potion",
            "nether/charge_respawn_anchor",
            "nether/create_beacon",
            "nether/create_full_beacon",
            "nether/distract_piglin",
            "nether/explore_nether",
            "nether/fast_travel",
            "nether/find_bastion",
            "nether/find_fortress",
            "nether/get_wither_skull",
            "nether/loot_bastion",
            "nether/netherite_armor",
            "nether/obtain_ancient_debris",
            "nether/obtain_blaze_rod",
            "nether/obtain_crying_obsidian",
            "nether/return_to_sender",
            "nether/ride_strider",
            "nether/root",
            "nether/summon_wither",
            "nether/uneasy_alliance",
            "nether/use_lodestone",

            "story/cure_zombie_villager",
            "story/deflect_arrow",
            "story/enchant_item",
            "story/enter_the_end",
            "story/enter_the_nether",
            "story/follow_ender_eye",
            "story/form_obsidian",
            "story/iron_tools",
            "story/lava_bucket",
            "story/mine_diamond",
            "story/mine_stone",
            "story/obtain_armor",
            "story/root",
            "story/shiny_gear",
            "story/smelt_iron",
            "story/upgrade_tools"
    };

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRandom(String string) {
        return string.equals(RANDOM);
    }

    public static boolean parseBoolean(String value) {
        List<String> list = Arrays.asList(BOOLEAN_LIST);
        if (!list.contains(value)) { return true; }
        return value.equals(ENABLED);
    }

    public static void quit(boolean close) {
        MinecraftClient client = MinecraftClient.getInstance();

        boolean bl = client.isInSingleplayer();
        boolean bl2 = client.isConnectedToRealms();

        if (client.world != null) { client.world.disconnect(); }

        if (bl) { client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel"))); }
        else { client.disconnect(); }

        if (bl && close) { client.openScreen(new TitleScreen()); }
        else if (bl2) {
            RealmsBridge realmsBridge = new RealmsBridge();
            realmsBridge.switchToRealms(new TitleScreen());
        }
        else { client.openScreen(new MultiplayerScreen(new TitleScreen())); }
    }

    public static int findTopPos(ServerWorld world, BlockPos blockPos) {
        int x = blockPos.getX();
        int z = blockPos.getZ();
        int i;
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = world.getBiome(mutable);
        boolean bl = world.getDimension().hasCeiling();
        BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
        WorldChunk worldChunk = world.getChunk(x >> 4, z >> 4);
        i = bl ? world.getChunkManager().getChunkGenerator().getSpawnHeight() : worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 0xF, z & 0xF);
        if (bl) {
            for (int k = i + 1; k >= 0; --k) {
                mutable.set(x, k, z);
                BlockState blockState2 = world.getBlockState(mutable);
                if (!blockState2.getFluidState().isEmpty()) break;
                if (!blockState2.equals(blockState)) continue;
                return mutable.up().toImmutable().getY();
            }
        } else {
            mutable.setY(i);
        }
        return mutable.getY();
    }

    public static BlockPos getRandomBlockInRadius(int radius, BlockPos blockPos, Random random) {
        return getRandomBlockInRadius(radius, 0, blockPos, random);
    }

    public static BlockPos getRandomBlockInRadius(int radius, int min, BlockPos blockPos, Random random) {
        BlockPos newPos;
        do {
            double ang = random.nextDouble() * 2 * Math.PI;
            double hyp = Math.sqrt(random.nextDouble()) * radius;
            double adj = Math.cos(ang) * hyp;
            double opp = Math.sin(ang) * hyp;
            newPos = new BlockPos(blockPos.getX() + adj, blockPos.getY(), blockPos.getZ() + opp);
        } while (newPos.isWithinDistance(blockPos, min));
        return newPos;
    }

    public static boolean hasAnyConfiguredInventories(@Nullable PracticeCategory except) {
        for (PracticeCategory category : PracticeCategoriesAny.ALL) {
            if (!category.equals(except) && !category.getCanHaveEmptyInventory() && category.hasConfiguredInventory()) {
                return true;
            }
        }
        return false;
    }

    public static String getNameFromId(String id) {
        StringBuilder text = new StringBuilder();
        boolean shouldCapitalise = true;
        for (Character c : id.toCharArray()) {
            if (shouldCapitalise) {
                text.append(c.toString().toUpperCase(Locale.ROOT));
                shouldCapitalise = false;
            } else if (c.equals('_')) {
                text.append(" ");
                shouldCapitalise = true;
            } else {
                text.append(c.toString().toLowerCase(Locale.ROOT));
            }
        }
        return text.toString();
    }
}
