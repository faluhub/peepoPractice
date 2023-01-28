package me.quesia.peepopractice.gui;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.CategorySettings;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.PracticeCategoryUtils;
import me.quesia.peepopractice.gui.inventory.EditInventoryScreen;
import me.quesia.peepopractice.gui.inventory.PlayerlessInventory;
import me.quesia.peepopractice.gui.inventory.PlayerlessPlayerScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.*;

public class CategorySettingsScreen extends Screen {
    private final Screen parent;
    private final PracticeCategory category;
    private final Map<ButtonWidget, String> descriptions = new HashMap<>();

    public CategorySettingsScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Configure (" + PracticeCategoryUtils.getName(category) + ")"));

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
        this.descriptions.clear();

        int index = 1;
        int offsetX = 200;
        int offsetY = 20;
        int spacingY = 30;
        int btnWidth = 200;

        this.addButton(
                new ButtonWidget(
                        this.width / 2 - 100 / 2,
                        this.height / 8,
                        100,
                        20,
                        new LiteralText("Edit Inventory"),
                        b -> {
                            if (this.client != null) {
                                PeepoPractice.PLAYERLESS_INVENTORY = new PlayerlessInventory();
                                PeepoPractice.PLAYERLESS_PLAYER_SCREEN_HANDLER = new PlayerlessPlayerScreenHandler();

                                this.client.openScreen(new EditInventoryScreen(this, this.category));
                            }
                        }
                )
        );

        for (CategorySettings setting : category.getSettings()) {
            String currentValue = CategorySettings.getValue(category, setting.getId());
            ButtonWidget button = this.addButton(
                    new ButtonWidget(
                            this.width / 2 - btnWidth / 2 - offsetX,
                            this.height / 4 + spacingY * index + offsetY,
                            btnWidth,
                            20,
                            new LiteralText(setting.getLabel() + ": " + currentValue),
                            b -> {
                                int currentIndex = this.getIndex(CategorySettings.getValue(category, setting.getId()), setting.getChoices());
                                String next;

                                try { next = setting.getChoices().get(currentIndex + 1); }
                                catch (IndexOutOfBoundsException ignored) { next = setting.getChoices().get(0); }

                                this.descriptions.remove(b);

                                b.setMessage(new LiteralText(setting.getLabel() + ": " + next));
                                CategorySettings.setValue(category, setting.getId(), next);

                                this.descriptions.put(b, setting.getDescription());
                            }
                    )
            );
            this.descriptions.put(button, setting.getDescription());

            index++;
        }

        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);

        for (AbstractButtonWidget button : this.buttons) {
            this.descriptions.forEach((v, k) -> {
                if (v.getMessage().getString().equals(button.getMessage().getString())) {
                    this.textRenderer.drawWithShadow(
                            matrices,
                            k,
                            v.x + v.getWidth() + 20,
                            v.y + v.getHeight() / 3.0F,
                            16777215
                    );
                }
            });
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (this.client != null) {
            this.client.openScreen(this.parent);

            if (this.parent instanceof CategorySelectionScreen) {
                ((CategorySelectionScreen) this.parent).selected = null;
            }
        }
    }
}
