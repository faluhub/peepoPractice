package me.quesia.peepopractice.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.utils.StandardSettingsUtils;
import me.quesia.peepopractice.gui.CustomOption;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import me.quesia.peepopractice.gui.widget.LimitlessDoubleOptionSliderWidget;
import me.quesia.peepopractice.mixin.access.ButtonWidgetAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class StandardSettingsScreen extends Screen {
    private static final Option[] OPTIONS = new Option[] {
            CustomOption.FOV,
            CustomOption.RENDER_DISTANCE,
            CustomOption.ENTITY_DISTANCE_SCALING,
            CustomOption.CHUNK_BORDERS,
            CustomOption.HITBOXES,
            CustomOption.TRIGGER
    };
    private final Screen parent;
    private TextFieldWidget pieChartDirectoryField;

    public StandardSettingsScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Standard Settings (" + category.getName(false) + ")"));
        this.parent = parent;
        PeepoPractice.CONFIGURING_CATEGORY = category;
    }

    @Override
    protected void init() {
        if (this.client == null) { return; }

        int index = 0;
        int btnIndex = 0;
        int btnWidth = this.width / 4;
        int btnHeight = this.height / 8;
        int yOffset = btnHeight / 8;
        boolean otherSide;
        for (Option option : OPTIONS) {
            otherSide = index + 1 > OPTIONS.length / 2;
            if (otherSide && index == OPTIONS.length / 2) { btnIndex = 0; }
            int x = this.width / 2 - btnWidth / 2 + (otherSide ? btnWidth / 2 : -btnWidth / 2);
            int y = 30 + (btnHeight + yOffset) * btnIndex;
            AbstractButtonWidget button = option.createButton(this.client.options, x, y, btnWidth);
            if (button instanceof ButtonWidget) {
                ButtonWidget.PressAction action = ((ButtonWidgetAccessor) button).getOnPress();
                button = new LimitlessButtonWidget(x, y, btnWidth, btnHeight, button.getMessage(), action);
            } else if (button instanceof DoubleOptionSliderWidget) {
                button = new LimitlessDoubleOptionSliderWidget(this.client.options, x, y, btnWidth, btnHeight, (DoubleOption) option);
            }
            this.addButton(button);
            btnIndex++;
            index++;
        }
        btnIndex++;
        this.pieChartDirectoryField = new TextFieldWidget(this.textRenderer, this.width / 2 - btnWidth, 30 + (btnHeight + yOffset) * (btnIndex - 1) + (20 + yOffset), btnWidth * 2, 20, new LiteralText("PieChart Directory"));
        this.pieChartDirectoryField.setMaxLength(99);
        this.pieChartDirectoryField.setText(StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "piechart", "root.gameRenderer.level.entities"));
        this.children.add(this.pieChartDirectoryField);

        this.addButton(new LimitlessButtonWidget(this.width / 2 - btnWidth, this.height - btnHeight - yOffset * 4, btnWidth * 2, btnHeight, ScreenTexts.DONE, b -> this.onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
        if (this.pieChartDirectoryField != null) {
            this.pieChartDirectoryField.render(matrices, mouseX, mouseY, delta);
        }
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        StandardSettingsUtils.setSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "piechart", this.pieChartDirectoryField.getText());
        PracticeWriter.STANDARD_SETTINGS_WRITER.write();
        PeepoPractice.CONFIGURING_CATEGORY = PracticeCategories.EMPTY;
        if (this.client != null) {
            this.client.openScreen(this.parent);
            return;
        }
        super.onClose();
    }
}
