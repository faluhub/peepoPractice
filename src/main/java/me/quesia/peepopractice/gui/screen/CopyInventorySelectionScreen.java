package me.quesia.peepopractice.gui.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.gui.widget.CategoryListWidget;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.Map;

public class CopyInventorySelectionScreen extends Screen {
    private final PracticeCategory category;
    private CategoryListWidget categoryListWidget;
    private ButtonWidget copyButton;

    public CopyInventorySelectionScreen(PracticeCategory category) {
        super(new LiteralText("Copy Inventory"));
        this.category = category;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.categoryListWidget != null && this.categoryListWidget.getSelected() != null) {
            this.categoryListWidget.setSelected(null);
            return false;
        }

        return super.shouldCloseOnEsc();
    }

    @Override
    protected void init() {
        this.categoryListWidget = new CategoryListWidget(this, this.client, true) {
            @Override
            public void onDoubleClick(PracticeCategory category) {
                CopyInventorySelectionScreen.this.copyInventory(category);
            }
        };
        this.children.add(this.categoryListWidget);

        this.addButton(
                new LimitlessButtonWidget(
                        null,
                        null,
                        null,
                        this.width / 2 - 155,
                        this.height - 50,
                        150,
                        40,
                        ScreenTexts.CANCEL,
                        b -> this.onClose()
                )
        );
        this.copyButton = this.addButton(
                new LimitlessButtonWidget(
                        null,
                        null,
                        null,
                        this.width / 2 - 155 + 160,
                        this.height - 50,
                        150,
                        40,
                        new LiteralText("Copy"),
                        b -> {
                            if (this.categoryListWidget != null && this.categoryListWidget.getSelected() != null) {
                                this.copyInventory(this.categoryListWidget.getSelected().category);
                                this.onClose();
                            }
                        }
                )
        );
    }

    public void copyInventory(PracticeCategory other) {
        PracticeWriter writer = PracticeWriter.INVENTORY_WRITER;
        JsonObject config = writer.get();
        boolean merge = Screen.hasShiftDown() && config.has(this.category.getId());
        if (config.has(other.getId())) {
            JsonObject inventory = config.get(other.getId()).getAsJsonObject();
            if (!merge) {
                writer.put(this.category.getId(), inventory);
            } else {
                JsonObject newInventory = config.get(this.category.getId()).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : inventory.entrySet()) {
                    newInventory.addProperty(entry.getKey(), entry.getValue().getAsString());
                }
                writer.put(this.category.getId(), newInventory);
            }
            writer.write();
        }
    }

    @Override
    public void onClose() {
        if (this.client != null) {
            this.client.openScreen(EditInventoryScreen.create(new CategorySelectionScreen(null), this.category));
            return;
        }

        super.onClose();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);

        if (this.categoryListWidget != null) {
            this.categoryListWidget.render(matrices, mouseX, mouseY, delta);
        }

        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);

        this.copyButton.active = this.categoryListWidget != null && this.categoryListWidget.getSelected() != null;
        if (this.copyButton.active) {
            this.copyButton.setMessage(!Screen.hasShiftDown() ? new LiteralText("Copy") : new LiteralText("Merge"));
        }

        super.render(matrices, mouseX, mouseY, delta);
    }
}
