package me.quesia.peepopractice.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.CategoryPreference;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.PracticeTypes;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class SplitSettingsScreen extends Screen {
    private final Screen parent;
    private final PracticeCategory category;
    private final CategoryPreference[] preferences = new CategoryPreference[] {
            new CategoryPreference()
                    .setId("compare_type")
                    .setLabel("Split Comparison")
                    .setDescription("Which type of time the split is compared against.")
                    .setChoices(PracticeTypes.CompareType.all())
                    .setDefaultChoice(PracticeTypes.CompareType.PB.getLabel())
                    .setIcon(new Identifier("textures/item/clock_00.png")),
            new CategoryPreference()
                    .setId("pace_timer_show_type")
                    .setLabel("Show Pace Timer")
                    .setDescription("When the pace timer should show.")
                    .setChoices(PracticeTypes.PaceTimerShowType.all())
                    .setDefaultChoice(PracticeTypes.PaceTimerShowType.ALWAYS.getLabel())
                    .setIcon(new Identifier("textures/mob_effect/blindness.png"))
    };

    public SplitSettingsScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Split Settings (" + category.getName(false) + ")"));
        this.parent = parent;
        this.category = category;
    }

    @Override
    protected void init() {
        int index = 0;
        int width = this.width / 2;
        int height = this.height / 5;
        int amount = this.preferences.length + 1;
        for (CategoryPreference preference : this.preferences) {
            String currentValue = CategoryPreference.getValue(this.category, preference);
            this.addButton(new LimitlessButtonWidget(false, preference.getIcon(), null, this.width / 2 - width / 2, this.height * (index + 1) / (amount + 1) - height / 2, width, height, new LiteralText(preference.getLabel() + ": " + currentValue), b -> {
                String value = CategoryPreference.getValue(this.category, preference);
                int currentIndex = CategoryPreference.getIndex(value, preference.getChoices());
                String next;

                try { next = preference.getChoices().get(currentIndex + 1); }
                catch (IndexOutOfBoundsException ignored) { next = preference.getChoices().get(0); }

                b.setMessage(new LiteralText(b.getMessage().getString().replaceAll(value, next)));
                CategoryPreference.setValue(this.category, preference.getId(), next);
            }));
            index++;
        }
        ButtonWidget bw = this.addButton(new LimitlessButtonWidget(false, new Identifier("textures/item/barrier.png"), null, this.width / 2 - width / 2, this.height * (index + 1) / (amount + 1) - height / 2, width, height, new LiteralText("Clear Saved Times"), b -> {
            this.category.getSplitEvent().clearTimes();
            b.active = false;
        }));
        bw.active = this.category.getSplitEvent().hasCompletedTimes();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
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
