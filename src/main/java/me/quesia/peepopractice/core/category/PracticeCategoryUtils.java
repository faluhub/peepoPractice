package me.quesia.peepopractice.core.category;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PracticeCategoryUtils {
    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    public static final String[] BOOLEAN_LIST = new String[] { ENABLED, DISABLED };
    public enum UniqueStructureState {
        NOT_GENERATED,
        GENERATED,
        IGNORED
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
