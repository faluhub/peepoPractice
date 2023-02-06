package me.quesia.peepopractice.core.category;

import com.google.gson.JsonObject;
import me.quesia.peepopractice.core.PracticeWriter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.List;

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

    public static boolean parseBoolean(String value) {
        List<String> list = Arrays.asList(BOOLEAN_LIST);
        if (!list.contains(value)) { return true; }
        return value.equals(ENABLED);
    }

    public static void quit(boolean close) {
        MinecraftClient client = MinecraftClient.getInstance();

        boolean bl = client.isInSingleplayer();
        boolean bl2 = client.isConnectedToRealms();

        if (client.world != null) { client.world.disconnect(); }

        if (bl) { client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel"))); }
        else { client.disconnect(); }

        if (bl && close) { client.openScreen(new TitleScreen()); }
        else if (bl2) {
            RealmsBridge realmsBridge = new RealmsBridge();
            realmsBridge.switchToRealms(new TitleScreen());
        }
        else { client.openScreen(new MultiplayerScreen(new TitleScreen())); }
    }
}
