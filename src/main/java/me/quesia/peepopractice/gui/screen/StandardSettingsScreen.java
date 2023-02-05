package me.quesia.peepopractice.gui.screen;

import me.quesia.peepopractice.core.category.PracticeCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;

public class StandardSettingsScreen extends Screen {
    private final Screen parent;

    public StandardSettingsScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Standard Settings (" + category.getName(false) + ")"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void onClose() {
        if (this.client != null) {
            this.client.openScreen(this.parent);
            return;
        }
        super.onClose();
    }
}
