package me.falu.peepopractice.gui.screen;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.PracticeWriter;
import me.falu.peepopractice.core.category.CategoryPreference;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;

public class CategoryPreferencesScreen extends Screen {
    private final Screen parent;
    private final PracticeCategory category;

    public CategoryPreferencesScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Configure (" + category.getName(false) + ")"));
        this.parent = parent;
        this.category = category;
    }

    private LiteralText getFormattedText(CategoryPreference preference, String currentValue) {
        String add = "";
        boolean isBoolValue = List.of(PracticeCategoryUtils.BOOLEAN_LIST).contains(currentValue);
        if (isBoolValue) {
            add += PracticeCategoryUtils.parseBoolean(currentValue) ? Formatting.GREEN : Formatting.RED;
        } else if (currentValue.equals(PracticeCategoryUtils.RANDOM)) {
            add += Formatting.YELLOW;
        }
        return new LiteralText(Formatting.BOLD + preference.getLabel() + ":\n" + Formatting.RESET + add + currentValue);
    }

    @Override
    protected void init() {
        if (this.client == null) { return; }

        int offset = this.width / 16;
        int size = this.width / 6 + offset;
        int column = 0;
        int row = 0;
        int values = Math.max(this.category.getPreferences().size(), 3);
        int maxColumns = Math.round(this.width / (float) (size * values)) + 2;

        for (CategoryPreference preference : this.category.getPreferences()) {
            String currentValue = CategoryPreference.getValue(this.category, preference.getId());
            this.addButton(
                    new LimitlessButtonWidget(
                            null,
                            preference.getIcon(),
                            (int) (32 * ((size - offset) / 110.0F)),
                            this.width * (column + 1) / (Math.max(maxColumns, 3) + 1) - size / 2,
                            32 + size * row,
                            size,
                            size - offset,
                            this.getFormattedText(preference, currentValue),
                            b -> {
                                String value = CategoryPreference.getValue(this.category, preference.getId());
                                if (value != null) {
                                    int currentIndex = CategoryPreference.getIndex(value, preference.getChoices());
                                    String next;

                                    try { next = preference.getChoices().get(currentIndex + 1); }
                                    catch (IndexOutOfBoundsException ignored) { next = preference.getChoices().get(0); }

                                    b.setMessage(this.getFormattedText(preference, next));
                                    CategoryPreference.setValue(this.category, preference.getId(), next);
                                }
                            },
                            (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, new LiteralText(preference.getDescription()), mouseX, mouseY)
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

            if (this.parent instanceof CategorySelectionScreen) {
                CategorySelectionScreen parent = ((CategorySelectionScreen) this.parent);
                if (parent.categoryListWidget != null) {
                    parent.categoryListWidget.setSelected(null);
                }
            }
        }
    }
}
