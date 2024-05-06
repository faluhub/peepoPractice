package me.falu.peepopractice.gui.screen;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.preferences.CategoryPreference;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.writer.PracticeWriter;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class CategoryPreferencesScreen extends Screen {
    private final Screen parent;
    private final PracticeCategory category;

    public CategoryPreferencesScreen(Screen parent, PracticeCategory category) {
        super(new TranslatableText("peepopractice.title.category_preferences", category.getSimpleName()));
        this.parent = parent;
        this.category = category;
    }

    public static Text getFormattedText(PracticeCategory category, CategoryPreference<?> preference) {
        return new LiteralText(Formatting.BOLD.toString())
                .append(preference.getLabel().copy().append(":\n").formatted(Formatting.BOLD))
                .append(Formatting.RESET.toString())
                .append(preference.getValueLabel(category, true));
    }

    @Override
    protected void init() {
        if (this.client == null) {
            return;
        }

        int offset = this.width / 16;
        int size = this.width / 6 + offset;
        int column = 0;
        int row = 0;
        int values = Math.max(this.category.getPreferences().size(), 3);
        int maxColumns = Math.round(this.width / (float) (size * values)) + 2;

        for (CategoryPreference<?> preference : this.category.getPreferences()) {
            this.addButton(
                    new LimitlessButtonWidget(
                            null,
                            preference.getIcon(),
                            (int) (32 * ((size - offset) / 110.0F)),
                            this.width * (column + 1) / (Math.max(maxColumns, 3) + 1) - size / 2,
                            32 + size * row,
                            size,
                            size - offset,
                            getFormattedText(this.category, preference),
                            b -> {
                                preference.advanceValue(this.category);
                                b.setMessage(getFormattedText(this.category, preference));
                            },
                            (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, preference.getDescription(), mouseX, mouseY)
                    )
            );

            column++;
            if (column >= maxColumns) {
                row++;
                column = 0;
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PeepoPractice.drawBackground(matrices, this);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        PracticeWriter.PREFERENCES_WRITER.write();
        if (this.client != null) {
            this.client.openScreen(this.parent);
        }
    }
}
