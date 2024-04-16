package me.falu.peepopractice.gui.screen;

import com.google.common.collect.Maps;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.CustomCategoryResourceManager;
import me.falu.peepopractice.core.category.PracticeCategoriesAA;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import me.falu.peepopractice.gui.widget.ThumbnailButtonWidget;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategorySelectionScreen extends Screen {
    private final Screen parent;
    private final List<PracticeCategory> cells;

    public CategorySelectionScreen(Screen parent) {
        super(new TranslatableText("peepopractice.title.category_selection"));
        this.parent = parent;
        this.cells = PeepoPractice.SELECTION_TYPE.list.stream().filter(v -> !v.getId().equals("empty")).collect(Collectors.toList());
    }

    @Override
    protected void init() {
        int rows = (int) Math.max(1, Math.ceil(Math.sqrt(this.cells.size())));
        int cols = (int) Math.max(1, Math.ceil(this.cells.size() / (float) rows));
        int paddingX = 8;
        int paddingY = 8;
        int gridWidth = this.width - this.width / 6;
        int gridHeight = this.height - this.height / 6;
        int cellWidth = Math.round((float) gridWidth / cols);
        int cellHeight = Math.round((float) gridHeight / rows);
        int configButtonSize = 20;

        for (int i = 0; i < this.cells.size(); i++) {
            PracticeCategory category = this.cells.get(i);
            int row = i / cols;
            int col = i % cols;
            int x = cellWidth * col + (this.width - gridWidth) / 2 + paddingX / 2;
            int y = cellHeight * row + paddingY / 2;
            int width = cellWidth - paddingX / 2;
            int height = cellHeight - paddingY / 2;
            this.addButton(
                    new ThumbnailButtonWidget(
                            x,
                            y,
                            width,
                            height,
                            category,
                            this.addButton(
                                    new ButtonWidget(
                                            x + width - configButtonSize - 4,
                                            y + 4,
                                            configButtonSize,
                                            configButtonSize,
                                            new LiteralText(""),
                                            b -> {
                                                if (this.client != null) {
                                                    this.client.openScreen(new SettingsTypeSelectionScreen(this, category));
                                                }
                                            },
                                            (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, new TranslatableText("peepopractice.button.configure"), mouseX, mouseY)
                                    )
                            ),
                            b -> {
                                if (this.client != null) {
                                    if (!category.hasConfiguredInventory()) {
                                        this.client.openScreen(new RealmsGenericErrorScreen(new TranslatableText("peepopractice.no_configured_inv"), new LiteralText(""), this));
                                        return;
                                    }
                                    PeepoPractice.CATEGORY = category;
                                    this.client.openScreen(new CreateWorldScreen(this));
                                }
                            }
                    )
            );
        }

        int minX = this.width;
        int maxX = 0;
        int maxY = 0;
        for (AbstractButtonWidget button : this.buttons) {
            if (button instanceof ThumbnailButtonWidget) {
                if (button.x < minX) {
                    minX = button.x;
                }
                if (button.x > maxX) {
                    maxX = button.x + button.getWidth();
                }
                if (button.y > maxY) {
                    maxY = button.y + button.getHeight();
                }
            }
        }
        Map<Text, ButtonWidget.PressAction> buttonRowItems = Maps.newLinkedHashMap();
        buttonRowItems.put(new LiteralText("<- ").append(ScreenTexts.DONE), b -> this.onClose());
        buttonRowItems.put(new TranslatableText("peepopractice.button.global_config"), b -> {
            if (this.client != null) {
                this.client.openScreen(new GlobalConfigScreen(this));
            }
        });
        buttonRowItems.put(new TranslatableText(PeepoPractice.SELECTION_TYPE.next().title).append(" ->"), b -> {
            if (this.client != null) {
                PeepoPractice.SELECTION_TYPE = PeepoPractice.SELECTION_TYPE.next();
                this.client.openScreen(new CategorySelectionScreen(this.parent));
            }
        });
        int buttonRowWidth = maxX - minX + paddingX;
        int buttonRowHeight = this.height - maxY;
        int buttonRowElemWidth = Math.round((float) buttonRowWidth / buttonRowItems.size()) - 1;
        int buttonRowElemHeight = buttonRowHeight / 2;
        int buttonRowElemY = maxY + buttonRowElemHeight / 2;

        int i = 0;
        for (Map.Entry<Text, ButtonWidget.PressAction> entry : buttonRowItems.entrySet()) {
            this.addButton(
                    new LimitlessButtonWidget(
                            minX + buttonRowElemWidth * i,
                            buttonRowElemY,
                            buttonRowElemWidth - paddingX / 2,
                            buttonRowElemHeight,
                            entry.getKey(),
                            entry.getValue()
                    )
            );
            i++;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PeepoPractice.drawBackground(matrices, this);

        String text = PeepoPractice.MOD_NAME + " v" + PeepoPractice.MOD_VERSION;
        this.textRenderer.drawWithShadow(matrices, text, this.width - this.textRenderer.getWidth(text), this.height - this.textRenderer.fontHeight, BackgroundHelper.ColorMixer.getArgb(255 / 2, 255, 255, 255));

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (this.client != null) {
            this.client.openScreen(this.parent);
        }
    }

    public enum SelectionType {
        ANY(PracticeCategoriesAny.ALL),
        AA(PracticeCategoriesAA.ALL),
        CUSTOM(CustomCategoryResourceManager.CUSTOM_CATEGORIES);
        public final String title;
        public final List<PracticeCategory> list;

        SelectionType(List<PracticeCategory> list) {
            this.title = "peepopractice.selection_type." + this.name().toLowerCase();
            this.list = list;
        }

        public static SelectionType next(SelectionType type) {
            int index = type.ordinal();
            while (true) {
                index++;
                int next = index >= values().length ? 0 : index;
                SelectionType value = values()[next];
                if (!value.list.isEmpty()) {
                    return value;
                }
            }
        }

        public SelectionType next() {
            return next(this);
        }
    }
}
