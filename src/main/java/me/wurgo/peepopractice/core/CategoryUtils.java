package me.wurgo.peepopractice.core;

import java.util.Arrays;
import java.util.List;

public class CategoryUtils {
    public static String[] booleanList = new String[] { "Enabled", "Disabled" };

    public static boolean parseBoolean(String option) {
        List<String> list = Arrays.asList(booleanList);
        if (!list.contains(option)) { return true; }
        return option.equals("Enabled");
    }

    public static String getName(PracticeCategory category) {
        StringBuilder text = new StringBuilder();
        int index = 0;

        for (char i : category.toString().toCharArray()) {
            String add = "";

            if (Character.isUpperCase(i) && index != 0) { add = " "; }
            add += Character.toString(i);

            text.append(add);
            index++;
        }

        return text.toString();
    }
}
