package me.quesia.peepopractice.core.category;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    public static boolean parseBoolean(String option) {
        List<String> list = Arrays.asList(BOOLEAN_LIST);
        if (!list.contains(option)) { return true; }
        return option.equals(ENABLED);
    }

    public static String getName(PracticeCategory category) {
        StringBuilder text = new StringBuilder();
        boolean shouldCapitalise = true;

        for (Character c : category.getId().toCharArray()) {
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
