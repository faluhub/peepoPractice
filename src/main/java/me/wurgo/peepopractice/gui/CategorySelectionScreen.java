package me.wurgo.peepopractice.gui;

import me.wurgo.peepopractice.PeepoPractice;
import me.wurgo.peepopractice.core.CategoryUtils;
import me.wurgo.peepopractice.core.PracticeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class CategorySelectionScreen extends Screen {
    public List<PracticeCategory> categories;
    public PracticeCategory selected;

    private final Screen parent;
    private CategoryListWidget categoryListWidget;
    private ButtonWidget doneButton;
    private ButtonWidget configureButton;

    public CategorySelectionScreen(Screen parent) {
        super(new LiteralText("Practice"));

        this.parent = parent;
        this.categories = Arrays.asList(PracticeCategory.values());
    }

    @Override
    protected void init() {
        this.categoryListWidget = new CategoryListWidget(this.client);
        this.children.add(this.categoryListWidget);

        this.doneButton = this.addButton(
                new ButtonWidget(
                        this.width / 2 - 155,
                        this.height - 40,
                        150,
                        20,
                        ScreenTexts.BACK,
                        b -> {
                            if (this.client != null) {
                                if (this.doneButton.getMessage().getString().equals(ScreenTexts.BACK.getString())) {
                                    this.client.openScreen(this.parent);
                                } else {
                                    if (this.selected != null) {
                                        PeepoPractice.CATEGORY = selected;
                                        this.client.openScreen(new CreateWorldScreen(this));
                                    }
                                }
                            }
                        }
                )
        );
        this.configureButton = this.addButton(
                new ButtonWidget(
                        this.width / 2 - 155 + 160,
                        this.height - 40,
                        150,
                        20,
                        new LiteralText("Configure"),
                        b -> {
                            if (this.client != null && this.selected != null) {
                                this.client.openScreen(new CategorySettingsScreen(this, this.selected));
                            }
                        }
                )
        );

        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.categoryListWidget != null) {
            this.categoryListWidget.render(matrices, mouseX, mouseY, delta);
        }

        this.drawCenteredText(
                matrices,
                this.textRenderer,
                this.title,
                this.width / 2,
                13,
                16777215
        );

        this.configureButton.active = this.selected != null;
        this.doneButton.setMessage(this.selected != null ? new LiteralText("Play!") : ScreenTexts.BACK);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (this.selected != null) {
            this.selected = null;
        } else {
            if (this.client != null) {
                this.client.openScreen(null);
            }
        }

        super.onClose();
    }

    private class CategoryListWidget extends AlwaysSelectedEntryListWidget<CategoryListWidget.CategoryEntry> {
        public CategoryListWidget(MinecraftClient client) {
            super(
                    client,
                    CategorySelectionScreen.this.width,
                    CategorySelectionScreen.this.height,
                    32,
                    CategorySelectionScreen.this.height - 64 + 4,
                    18
            );

            for (PracticeCategory category : CategorySelectionScreen.this.categories) {
                this.addEntry(new CategoryEntry(category));
            }

            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        protected int getScrollbarPositionX() { return super.getScrollbarPositionX() + 20; }

        public int getRowWidth() { return super.getRowWidth() + 50; }

        public void setSelected(CategoryEntry categoryEntry) {
            super.setSelected(categoryEntry);
            if (categoryEntry != null) { CategorySelectionScreen.this.selected = categoryEntry.category; }
            else { CategorySelectionScreen.this.selected = null; }
        }

        protected void renderBackground(MatrixStack matrices) { CategorySelectionScreen.this.renderBackground(matrices); }

        protected boolean isFocused() { return CategorySelectionScreen.this.getFocused() == this; }

        public class CategoryEntry extends AlwaysSelectedEntryListWidget.Entry<CategoryListWidget.CategoryEntry> {
            public final PracticeCategory category;

            public CategoryEntry(PracticeCategory category) {
                this.category = category;
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                Text label = new LiteralText(CategoryUtils.getName(this.category));

                CategorySelectionScreen.this.textRenderer.drawWithShadow(
                        matrices,
                        label.getString(),
                        (float) (CategorySelectionScreen.this.width / 2 - CategorySelectionScreen.this.textRenderer.getWidth(label.getString()) / 2),
                        y + 1,
                        16777215,
                        true
                );
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                } else { return false; }
            }

            public void onPressed() {
                if (CategoryListWidget.this.getSelected() == this) { CategoryListWidget.this.setSelected(null); }
                else { CategoryListWidget.this.setSelected(this); }
            }
        }
    }
}
