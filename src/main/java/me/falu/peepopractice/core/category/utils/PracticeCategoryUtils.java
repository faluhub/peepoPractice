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
