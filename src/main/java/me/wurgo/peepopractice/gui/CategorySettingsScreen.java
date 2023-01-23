package me.wurgo.peepopractice.gui;

import me.wurgo.peepopractice.PeepoPractice;
import me.wurgo.peepopractice.core.CategorySettings;
import me.wurgo.peepopractice.core.CategoryUtils;
import me.wurgo.peepopractice.core.PracticeCategory;
import me.wurgo.peepopractice.gui.inventory.EditInventoryScreen;
import me.wurgo.peepopractice.gui.inventory.PlayerlessInventory;
import me.wurgo.peepopractice.gui.inventory.PlayerlessPlayerScreenHandler;
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

    protected CategorySettingsScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Configure (" + CategoryUtils.getName(category) + ")"));

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
                                PeepoPractice.playerlessInventory = new PlayerlessInventory();
                                PeepoPractice.playerlessPlayerScreenHandler = new PlayerlessPlayerScreenHandler();
                                this.client.openScreen(new EditInventoryScreen());
                            }
                        }
                )
        );

        for (CategorySettings setting : category.settings) {
            String currentValue = CategorySettings.getValue(setting.id, category.settings);
            ButtonWidget button = this.addButton(
                    new ButtonWidget(
                            this.width / 2 - btnWidth / 2 - offsetX,
                            this.height / 4 + spacingY * index + offsetY,
                            btnWidth,
                            20,
                            new LiteralText(setting.label + ": " + currentValue),
                            b -> {
                                int currentIndex = this.getIndex(CategorySettings.getValue(setting.id, category.settings), setting.options);
                                String next;

                                try { next = setting.options.get(currentIndex + 1); }
                                catch (IndexOutOfBoundsException ignored) { next = setting.options.get(0); }

                                this.descriptions.remove(b);

                                b.setMessage(new LiteralText(setting.label + ": " + next));
                                CategorySettings.setValue(setting.id, next);

                                this.descriptions.put(b, setting.description);
                            }
                    )
            );
            this.descriptions.put(button, setting.description);

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
            this.client.openScreen(parent);

            if (this.parent instanceof CategorySelectionScreen) {
                ((CategorySelectionScreen) this.parent).selected = null;
            }
        }

        super.onClose();
    }
}
