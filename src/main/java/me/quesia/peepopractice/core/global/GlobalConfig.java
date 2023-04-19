package me.quesia.peepopractice.core.global;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.quesia.peepopractice.core.PracticeWriter;

public class GlobalConfig {
    public static int getIntValue(String key, int def) {
        JsonObject object = PracticeWriter.GLOBAL_CONFIG.get();
        if (!object.has(key)) {
            PracticeWriter.GLOBAL_CONFIG.put(key, new JsonPrimitive(def));
            PracticeWriter.GLOBAL_CONFIG.update();
            return def;
        }
        return object.get(key).getAsInt();
    }

    public static boolean getBoolValue(String key, boolean def) {
        JsonObject object = PracticeWriter.GLOBAL_CONFIG.get();
        if (!object.has(key)) {
            PracticeWriter.GLOBAL_CONFIG.put(key, new JsonPrimitive(def));
            PracticeWriter.GLOBAL_CONFIG.update();
            return def;
        }
        return object.get(key).getAsBoolean();
    }
}
