package me.falu.peepopractice.core.category.preferences;

import lombok.Getter;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PreferenceTypes {
    public enum BooleanType {
        ENABLED,
        DISABLED
    }

    public enum BooleanRandomType {
        ENABLED,
        DISABLED,
        RANDOM
    }

    public enum BastionType implements ConfigTypeBase {
        HOUSING(0, new Vec3i(-9, 83, 27), -90.0F),
        STABLES(1, new Vec3i(3, 54, 30), 90.0F),
        TREASURE(2, new Vec3i(16, 75, -1), 180.0F),
        BRIDGE(3, new Vec3i(-26, 67, 10), -90.0F),
        RANDOM(4, new Vec3i(0, 0, 0), 0.0F);
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

        @Override
        public String getIdentifier() {
            return "bastion";
        }
    }

    public enum CompareType implements ConfigTypeBase {
        PB,
        AVERAGE;

        @Override
        public String getIdentifier() {
            return "compare";
        }
    }

    public enum PaceTimerShowType implements ConfigTypeBase {
        ALWAYS,
        END,
        NEVER;

        @Override
        public String getIdentifier() {
            return "pace_timer_show";
        }
    }

    public enum EyeCountType implements ConfigTypeBase {
        ALL,
        RANDOM,
        NONE;

        @Override
        public String getIdentifier() {
            return "eye_count";
        }
    }

    @Getter
    public enum StrongholdDistanceType implements ConfigTypeBase {
        CLOSE(200, 500),
        AVERAGE(700, 1000),
        FAR(1200, 1600),
        RANDOM(0, 2300);
        private final int min;
        private final int max;

        StrongholdDistanceType(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String getIdentifier() {
            return "stronghold_distance";
        }
    }

    public enum StartNodeType implements ConfigTypeBase {
        RANDOM,
        FRONT,
        BACK;

        @Override
        public String getIdentifier() {
            return "start_node";
        }
    }

    public enum SpawnLocationType implements ConfigTypeBase {
        STRUCTURE,
        TERRAIN;

        @Override
        public String getIdentifier() {
            return "spawn_location";
        }
    }

    public enum EndTowerHeightType implements ConfigTypeBase {
        RANDOM(),
        SMALL_BOY_76(76),
        SMALL_CAGE_79(79, true),
        TALL_CAGE_82(82, true),
        M85(85),
        M88(88),
        M91(91),
        T94(94),
        T97(97),
        T100(100),
        TALL_BOY_103(103);
        public final int height;
        public final boolean caged;

        EndTowerHeightType(int height, boolean caged) {
            this.height = height;
            this.caged = caged;
        }

        EndTowerHeightType(int height) {
            this(height, false);
        }

        EndTowerHeightType() {
            this.height = -1;
            this.caged = false;
        }

        @Override
        public String getIdentifier() {
            return "end_tower_height";
        }
    }

    public enum SelectedInventoryType {
        ONE,
        TWO,
        THREE
    }

    public enum PostBlindSpawnDimensionType implements ConfigTypeBase {
        OVERWORLD(World.OVERWORLD),
        NETHER(World.NETHER);
        public final RegistryKey<World> key;

        PostBlindSpawnDimensionType(RegistryKey<World> key) {
            this.key = key;
        }

        @Override
        public String getIdentifier() {
            return "post_blind_spawn_dimension";
        }
    }

    public interface ConfigTypeBase {
        String getIdentifier();
    }
}
