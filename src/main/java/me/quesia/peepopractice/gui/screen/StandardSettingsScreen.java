package me.quesia.peepopractice.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.StandardSettingsUtils;
import me.quesia.peepopractice.gui.CustomOption;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class StandardSettingsScreen extends Screen {
    private static final Option[] OPTIONS = new Option[] {
            CustomOption.FOV,
            CustomOption.RENDER_DISTANCE,
            CustomOption.ENTITY_DISTANCE_SCALING,
            CustomOption.SPRINTING,
            CustomOption.CHUNK_BORDERS,
            CustomOption.FULLSCREEN
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
        int btnWidth = 170;
        for (Option option : OPTIONS) {
            this.addButton(option.createButton(this.client.options, this.width / 2 - btnWidth / 2, 30 + (20 + 5) * index, btnWidth));
            index++;
        }
        this.pieChartDirectoryField = new TextFieldWidget(this.textRenderer, this.width / 2 - btnWidth / 2, 30 + (20 + 5) * index, btnWidth, 20, new LiteralText("PieChart Directory"));
        this.pieChartDirectoryField.setMaxLength(99);
        this.pieChartDirectoryField.setText(StandardSettingsUtils.getSettingForCategory(PeepoPractice.CONFIGURING_CATEGORY, "piechart", "root.gameRenderer.level.entities"));
        this.children.add(this.pieChartDirectoryField);
        index++;
        this.addButton(new LimitlessButtonWidget(null, null, null, this.width / 2 - btnWidth / 2, 30 + (20 + 5) * index, btnWidth, 20, ScreenTexts.DONE, b -> this.onClose()));
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
