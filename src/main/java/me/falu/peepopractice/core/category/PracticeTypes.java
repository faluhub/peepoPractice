package me.falu.peepopractice.core.category;

import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class PracticeTypes {
    public enum BastionType {
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

        public String getLabel() {
            return PracticeCategoryUtils.getNameFromId(this.name());
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (BastionType type : BastionType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public static BastionType fromLabel(String label) {
            for (BastionType type : BastionType.values()) {
                if (type.getLabel().equals(label)) {
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
        ALWAYS,
        END,
        NEVER;

        public String getLabel() {
            return PracticeCategoryUtils.getNameFromId(this.name());
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

    public enum EyeCountType {
        ALL,
        RANDOM,
        NONE;

        public String getLabel() {
            return PracticeCategoryUtils.getNameFromId(this.name());
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (EyeCountType type : EyeCountType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public static EyeCountType fromLabel(String label) {
            for (EyeCountType type : EyeCountType.values()) {
                if (type.getLabel().equals(label)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum StrongholdDistanceType {
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

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        public String getLabel() {
            return PracticeCategoryUtils.getNameFromId(this.name());
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (StrongholdDistanceType type : StrongholdDistanceType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public static StrongholdDistanceType fromLabel(String label) {
            for (StrongholdDistanceType type : StrongholdDistanceType.values()) {
                if (type.getLabel().equals(label)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum StartNodeType {
        RANDOM,
        FRONT,
        BACK;

        public String getLabel() {
            return PracticeCategoryUtils.getNameFromId(this.name());
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (StartNodeType type : StartNodeType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public static StartNodeType fromLabel(String label) {
            for (StartNodeType type : StartNodeType.values()) {
                if (type.getLabel().equals(label)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum SpawnLocationType {
        STRUCTURE,
        TERRAIN;

        public String getLabel() {
            return PracticeCategoryUtils.getNameFromId(this.name());
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (SpawnLocationType type : SpawnLocationType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public static SpawnLocationType fromLabel(String label) {
            for (SpawnLocationType type : SpawnLocationType.values()) {
                if (type.getLabel().equals(label)) {
                    return type;
                }
            }
            return null;
        }
    }
}
