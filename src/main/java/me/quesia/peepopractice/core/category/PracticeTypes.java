package me.quesia.peepopractice.core.category;

import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PracticeTypes {
    private static String parseEnumName(String name) {
        StringBuilder text = new StringBuilder();
        boolean shouldCapitalise = true;
        for (Character c : name.toCharArray()) {
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
            return parseEnumName(this.name());
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
            return parseEnumName(this.name());
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
            return parseEnumName(this.name());
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
        RANDOM(200, 1600);

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
            return parseEnumName(this.name());
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
}
