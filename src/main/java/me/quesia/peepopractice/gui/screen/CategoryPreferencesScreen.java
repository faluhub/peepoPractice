package me.quesia.peepopractice.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.CategoryPreference;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.playerless.PlayerlessInventory;
import me.quesia.peepopractice.core.playerless.PlayerlessPlayerScreenHandler;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;

import java.util.*;

public class CategoryPreferencesScreen extends Screen {
    private final Screen parent;
    private final PracticeCategory category;

    public CategoryPreferencesScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Configure (" + category.getName(false) + ")"));

        this.parent = parent;
        this.category = category;
    }

    private int getIndex(String value, List<String> choices) {
        int index = 0;

        for (String choice : choices) {
            if (Objects.equals(value, choice)) {
                break;
            }
            index++;
        }

        return index;
    }

    @Override
    protected void init() {
        if (this.client == null) { return; }

        int size = 110;
        int column = 0;
        int row = 0;
        int values = this.category.getPreferences().size();
        int maxColumns = Math.round(this.width / (float) (size * values)) + 2;

        for (CategoryPreference preference : this.category.getPreferences()) {
            String currentValue = CategoryPreference.getValue(this.category, preference.getId());
            this.addButton(
                    new LimitlessButtonWidget(
                            null,
                            preference.getIcon(),
                            null,
                            this.width * (column + 1) / (maxColumns + 1) - size / 2 + column * 3,
                            32 + size * row,
                            size,
                            size,
                            new LiteralText(preference.getLabel() + ":\n" + currentValue),
                            b -> {
                                int currentIndex = this.getIndex(CategoryPreference.getValue(this.category, preference.getId()), preference.getChoices());
                                String next;

                                try { next = preference.getChoices().get(currentIndex + 1); }
                                catch (IndexOutOfBoundsException ignored) { next = preference.getChoices().get(0); }

                                b.setMessage(new LiteralText(preference.getLabel() + ":\n" + next));
                                CategoryPreference.setValue(this.category, preference.getId(), next);
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
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        PracticeWriter.PREFERENCES_WRITER.write();
        if (this.client != null) {
            this.client.openScreen(this.parent);

            if (this.parent instanceof CategorySelectionScreen) {
                ((CategorySelectionScreen) this.parent).selected = null;
            }
        }
    }
}
