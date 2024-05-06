package me.falu.peepopractice.gui.screen;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.preferences.CategoryPreference;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.writer.PracticeWriter;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class SplitSettingsScreen extends Screen {
    private static final CategoryPreference<?>[] PREFERENCES = new CategoryPreference[] {
            CategoryPreferences.COMPARE_TYPE,
            CategoryPreferences.PACE_TIMER_SHOW_TYPE
    };
    private final Screen parent;
    private final PracticeCategory category;

    public SplitSettingsScreen(Screen parent, PracticeCategory category) {
        super(new TranslatableText("peepopractice.title.split_settings", category.getSimpleName()));
        this.parent = parent;
        this.category = category;
    }

    @Override
    protected void init() {
        int index = 0;
        int width = this.width / 2;
        int height = this.height / 5;
        int amount = PREFERENCES.length + 1;
        for (CategoryPreference<?> preference : PREFERENCES) {
            this.addButton(
                    new LimitlessButtonWidget(
                            false,
                            preference.getIcon(),
                            null,
                            this.width / 2 - width / 2,
                            this.height * (index + 1) / (amount + 1) - height / 2,
                            width,
                            height,
                            preference.getLabel().copy().append(": ").append(preference.getValueLabel(this.category)),
                            b -> {
                                preference.advanceValue(this.category);
                                b.setMessage(preference.getLabel().copy().append(": ").append(preference.getValueLabel(this.category)));
                            }
                    )
            );
            index++;
        }
        ButtonWidget bw = this.addButton(new LimitlessButtonWidget(false, new Identifier("textures/item/barrier.png"), null, this.width / 2 - width / 2, this.height * (index + 1) / (amount + 1) - height / 2, width, height, new TranslatableText("peepopractice.button.clear_saved_times"), b -> {
            this.category.getSplitEvent().clearTimes();
            b.active = false;
        }));
        bw.active = this.category.getSplitEvent().hasCompletedTimes();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PeepoPractice.drawBackground(matrices, this);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
    }

    @Override
    public void onClose() {
        PracticeWriter.PREFERENCES_WRITER.write();
        if (this.client != null) {
            this.client.openScreen(this.parent);
            return;
        }
        super.onClose();
    }
}
