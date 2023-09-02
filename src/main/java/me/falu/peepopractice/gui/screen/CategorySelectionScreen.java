package me.falu.peepopractice.gui.screen;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.PracticeWriter;
import me.falu.peepopractice.core.category.PracticeCategoriesAA;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.gui.widget.CategoryListWidget;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.List;

public class CategorySelectionScreen extends Screen {
    private final Screen parent;
    private SelectionType selectionType = SelectionType.ANY;
    public CategoryListWidget categoryListWidget;
    private ButtonWidget doneButton;
    private ButtonWidget configureButton;

    public CategorySelectionScreen(Screen parent) {
        super(new LiteralText("Select Practice Category"));
        this.parent = parent;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.categoryListWidget != null && this.categoryListWidget.getSelected() != null) {
            this.categoryListWidget.setSelected(null);
            return false;
        }

        return super.shouldCloseOnEsc();
    }

    public void openConfig() {
        if (this.client != null) {
            if (this.categoryListWidget != null && this.categoryListWidget.getSelected() != null) {
                this.client.openScreen(new SettingsTypeSelectionScreen(this, this.categoryListWidget.getSelected().category));
            } else {
                this.client.openScreen(new GlobalConfigScreen(this));
            }
        }
    }

    public void play() {
        if (this.client != null && this.categoryListWidget != null && this.categoryListWidget.getSelected() != null) {
            PracticeCategory selected = this.categoryListWidget.getSelected().category;
            if (!selected.hasConfiguredInventory()) {
                this.client.openScreen(new FatalErrorScreen(new LiteralText("You haven't configured your inventory for this category yet!"), new LiteralText("")));
                return;
            }
            PeepoPractice.CATEGORY = selected;
            this.client.openScreen(new CreateWorldScreen(this));
        }
    }

    @Override
    protected void init() {
        this.children.clear();
        this.buttons.clear();

        this.categoryListWidget = new CategoryListWidget(this, this.client, false, false, this.selectionType) {
            @Override
            public void onDoubleClick(PracticeCategory category) {
                CategorySelectionScreen.this.play();
            }
        };
        this.children.add(this.categoryListWidget);

        this.doneButton = this.addButton(
                new LimitlessButtonWidget(
                        null,
                        null,
                        null,
                        this.width / 2 - 155,
                        this.height - 50,
                        150,
                        40,
                        ScreenTexts.BACK,
                        b -> {
                            if (this.client != null) {
                                if (this.doneButton.getMessage().getString().equals(ScreenTexts.BACK.getString())) {
                                    this.client.openScreen(this.parent);
                                } else {
                                    this.play();
                                }
                            }
                        }
                )
        );
        this.configureButton = this.addButton(
                new LimitlessButtonWidget(
                        null,
                        null,
                        null,
                        this.width / 2 - 155 + 160,
                        this.height - 50,
                        150,
                        40,
                        new LiteralText("Global Config"),
                        b -> this.openConfig()
                )
        );
        this.addButton(
                new LimitlessButtonWidget(
                        null,
                        null,
                        null,
                        5,
                        5,
                        40,
                        20,
                        new LiteralText(this.selectionType.title),
                        b -> {
                            this.selectionType = SelectionType.opposite(this.selectionType);
                            this.init();
                        }
                )
        );
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PeepoPractice.drawBackground(matrices, this);

        if (this.categoryListWidget != null) {
            this.categoryListWidget.render(matrices, mouseX, mouseY, delta);
            this.fillGradient(matrices, 0, 0, this.width, this.categoryListWidget.getTop(), PeepoPractice.BACKGROUND_COLOR[0], PeepoPractice.BACKGROUND_COLOR[0]);
            this.fillGradient(matrices, 0, this.categoryListWidget.getBottom(), this.width, this.height, PeepoPractice.BACKGROUND_COLOR[1], PeepoPractice.BACKGROUND_COLOR[1]);
        }

        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
        String text = PeepoPractice.MOD_NAME + " v" + PeepoPractice.MOD_VERSION;
        this.textRenderer.drawWithShadow(matrices, text, this.width - this.textRenderer.getWidth(text), this.height - this.textRenderer.fontHeight, BackgroundHelper.ColorMixer.getArgb(255 / 2, 255, 255, 255));

        boolean selected = this.categoryListWidget != null && this.categoryListWidget.getSelected() != null;
        this.configureButton.setMessage(new LiteralText(selected ? "Configure" : "Global Config"));
        this.doneButton.setMessage(selected ? new LiteralText("Play!") : ScreenTexts.BACK);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        PracticeWriter.COMPLETIONS_WRITER.update();

        if (this.categoryListWidget != null && this.categoryListWidget.getSelected() != null) {
            this.categoryListWidget.setSelected(null);
        } else {
            if (this.client != null) {
                this.client.openScreen(null);
            }
        }
    }

    public enum SelectionType {
        ANY("Any%", PracticeCategoriesAny.ALL),
        AA("AA", PracticeCategoriesAA.ALL);

        public final String title;
        public final List<PracticeCategory> list;

        SelectionType(String title, List<PracticeCategory> list) {
            this.title = title;
            this.list = list;
        }

        public static SelectionType opposite(SelectionType type) {
            if (type.equals(ANY)) { return AA; }
            else { return ANY; }
        }
    }
}
