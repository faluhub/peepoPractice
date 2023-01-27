package me.quesia.peepopractice.core.category;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PracticeCategoryUtils {
    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    public static final String[] BOOLEAN_LIST = new String[] { ENABLED, DISABLED };

    public static boolean parseBoolean(String option) {
        List<String> list = Arrays.asList(BOOLEAN_LIST);
        if (!list.contains(option)) { return true; }
        return option.equals(ENABLED);
    }

    public static String getName(PracticeCategory category) {
        StringBuilder text = new StringBuilder();
        int index = 0;

        for (char i : category.getId().toCharArray()) {
            String add = "";

            if (Character.isUpperCase(i) && index != 0) { add = " "; }
            add += Character.toString(i);

            text.append(index != 0 ? add : add.toUpperCase(Locale.ROOT));
            index++;
        }

        return text.toString();
    }
}
