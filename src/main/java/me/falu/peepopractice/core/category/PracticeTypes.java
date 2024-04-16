package me.falu.peepopractice.core.category;

import lombok.Getter;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class PracticeTypes {
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T getTypeValue(PracticeCategory category, String id, T def) {
        String value = CategoryPreference.getValue(category, id, def.toString());
        for (Enum<?> v : def.getDeclaringClass().getEnumConstants()) {
            if (v.toString().equals(value) && v.getDeclaringClass().equals(def.getDeclaringClass())) {
                return (T) v;
            }
        }
        return def;
    }

    public static <T extends Enum<?>> T getTypeValue(String id, T def) {
        return getTypeValue(PeepoPractice.CATEGORY, id, def);
    }

    private static String translate(String type, String key) {
        return "peepopractice.types." + type + "." + key;
    }

    @SuppressWarnings("SameReturnValue")
    private static String random() {
        return "peepopractice.text.random";
    }

    public static String getNameFromId(String id) {
        StringBuilder name = new StringBuilder();
        boolean capitalize = true;
        for (char i : id.toLowerCase().toCharArray()) {
            if (i == '_') {
                capitalize = true;
                name.append(" ");
                continue;
            }
            name.append(capitalize ? Character.toUpperCase(i) : i);
            capitalize = false;
        }
        return name.toString();
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

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (BastionType type : BastionType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return this != RANDOM ? translate("bastion", this.name().toLowerCase()) : random();
        }
    }

    public enum CompareType {
        PB,
        AVERAGE;

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (CompareType type : CompareType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return translate("compare", this.name().toLowerCase());
        }
    }

    public enum PaceTimerShowType {
        ALWAYS,
        END,
        NEVER;

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (PaceTimerShowType type : PaceTimerShowType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return translate("pace_timer_show", this.name().toLowerCase());
        }
    }

    public enum EyeCountType {
        ALL,
        RANDOM,
        NONE;

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (EyeCountType type : EyeCountType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return this != RANDOM ? translate("eye_count", this.name().toLowerCase()) : random();
        }
    }

    @Getter
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

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (StrongholdDistanceType type : StrongholdDistanceType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return this != RANDOM ? translate("stronghold_distance", this.name().toLowerCase()) : random();
        }
    }

    public enum StartNodeType {
        RANDOM,
        FRONT,
        BACK;

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (StartNodeType type : StartNodeType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return this != RANDOM ? translate("start_node", this.name().toLowerCase()) : random();
        }
    }

    public enum SpawnLocationType {
        STRUCTURE,
        TERRAIN;

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (SpawnLocationType type : SpawnLocationType.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return translate("spawn_location", this.name().toLowerCase());
        }
    }

    public enum EndTowerHeights {
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
        private final String name;
        public final int height;
        public final boolean caged;

        EndTowerHeights(int height, boolean caged) {
            this.name = getNameFromId(this.name());
            this.height = height;
            this.caged = caged;
        }

        EndTowerHeights(int height) {
            this(height, false);
        }

        EndTowerHeights() {
            this.name = PracticeCategoryUtils.RANDOM;
            this.height = -1;
            this.caged = false;
        }

        public static List<String> all() {
            List<String> labels = new ArrayList<>();
            for (EndTowerHeights type : EndTowerHeights.values()) {
                labels.add(type.getLabel());
            }
            return labels;
        }

        public String getLabel() {
            return this.name;
        }
    }
}
