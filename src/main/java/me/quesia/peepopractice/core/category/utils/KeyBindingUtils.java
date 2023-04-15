package me.quesia.peepopractice.core.category.utils;

import com.google.common.collect.Lists;
import me.quesia.peepopractice.mixin.access.KeyBindingAccessor;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KeyBindingUtils {
    public static final List<KeyBinding> KEY_BINDINGS = Lists.newArrayList();

    public static Text getTranslation(String path, String text) {
        return (Language.getInstance().get(path).equals(path) ? new LiteralText(text) : new TranslatableText(path));
    }

    private static Map<String, Integer> getCategoryMap() {
        return KeyBindingAccessor.peepoPractice$getCategoryMap();
    }

    private static boolean noCategory(String key) {
        return !getCategoryMap().containsKey(key);
    }

    public static void addCategory(String key) {
        Map<String, Integer> map = getCategoryMap();
        if (noCategory(key)) {
            Optional<Integer> largest = map.values().stream().max(Integer::compare);
            int largestInt = largest.orElse(0);
            map.put(key, largestInt + 1);
        }
    }

    public static KeyBinding registerKeyBinding(KeyBinding keyBinding) {
        if (noCategory(keyBinding.getCategory())) {
            addCategory(keyBinding.getCategory());
        }

        KEY_BINDINGS.add(keyBinding);
        return keyBinding;
    }

    public static KeyBinding[] process(KeyBinding[] all) {
        List<KeyBinding> keyBindings = Lists.newArrayList(all);
        keyBindings.removeAll(KEY_BINDINGS);
        keyBindings.addAll(KEY_BINDINGS);
        return keyBindings.toArray(new KeyBinding[0]);
    }
}
