package me.falu.peepopractice.core.global;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.falu.peepopractice.core.PracticeWriter;

public class GlobalConfig {
    public static double getDoubleValue(String key, double def) {
        JsonObject object = PracticeWriter.GLOBAL_CONFIG.get();
        if (!object.has(key)) {
            PracticeWriter.GLOBAL_CONFIG.put(key, new JsonPrimitive(def));
            return def;
        }
        return object.get(key).getAsDouble();
    }

    public static boolean getBoolValue(String key, boolean def) {
        JsonObject object = PracticeWriter.GLOBAL_CONFIG.get();
        if (!object.has(key)) {
            PracticeWriter.GLOBAL_CONFIG.put(key, new JsonPrimitive(def));
            return def;
        }
        return object.get(key).getAsBoolean();
    }
}
