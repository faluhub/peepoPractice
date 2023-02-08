package me.quesia.peepopractice.core.category;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PracticeCategoryUtils {
    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    public static final String[] BOOLEAN_LIST = new String[] { ENABLED, DISABLED };

    public enum BastionType {
        HOUSING(0, new Vec3i(-9, 83, 27), -90.0F),
        STABLES(1, new Vec3i(3, 54, 30), 90.0F),
        TREASURE(2, new Vec3i(16, 75, -1), 180.0F),
        BRIDGE(3, new Vec3i(-26, 67, 10), -90.0F);

        public final int id;
        public final Vec3i pos;
        public final float angle;

        BastionType(int id, Vec3i pos, float angle) {
            this.id = id;
            this.pos = pos;
            this.angle = angle;
        }

        public static BastionType fromId(int id) {
            for (BastionType type : BastionType.values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum CompareType {
        PB("PB"),
        AVERAGE("Average");

        private final String label;

        CompareType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (CompareType type : CompareType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public static CompareType fromLabel(String label) {
            for (CompareType type : CompareType.values()) {
                if (type.getLabel().equals(label)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum PaceTimerShowType {
        ALWAYS("Always"),
        END("End"),
        NEVER("Never");

        private final String label;

        PaceTimerShowType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (PaceTimerShowType type : PaceTimerShowType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public static PaceTimerShowType fromLabel(String label) {
            for (PaceTimerShowType type : PaceTimerShowType.values()) {
                if (type.getLabel().equals(label)) {
                    return type;
                }
            }
            return null;
        }
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
        worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 0xF, z & 0xF);
        for (int k = i + 1; k >= 0; --k) {
            mutable.set(x, k, z);
            BlockState blockState2 = world.getBlockState(mutable);
            if (!blockState2.getFluidState().isEmpty()) break;
            if (!blockState2.equals(blockState)) continue;
            return mutable.up().toImmutable().getY();
        }
        return mutable.getY();
    }
}
