package me.falu.peepopractice.gui.screen;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class SettingsTypeSelectionScreen extends Screen {
    private final ButtonChoice[] buttons = new ButtonChoice[] {
            new ButtonChoice(new LiteralText("Inventory"), new Identifier("textures/item/chest_minecart.png"), category -> EditInventoryScreen.create(this, category)),
            new ButtonChoice(new LiteralText("Preferences"), new Identifier("textures/item/heart_of_the_sea.png"), category -> new CategoryPreferencesScreen(this, category), new ButtonDisabledInfo() {
                @Override
                public boolean isDisabled(PracticeCategory category) {
                    return !category.hasPreferences();
                }

                @Override
                public String getReason() {
                    return "This category has no preferences to configure.";
                }
            }),
            new ButtonChoice(new LiteralText("Standard Settings"), new Identifier(PeepoPractice.MOD_ID, "icon/gear.png"), category -> new StandardSettingsScreen(this, category)),
            new ButtonChoice(new LiteralText("Split"), new Identifier("textures/item/clock_00.png"), category -> new SplitSettingsScreen(this, category), new ButtonDisabledInfo() {
                @Override
                public boolean isDisabled(PracticeCategory category) {
                    return !category.hasSplitEvent();
                }

                @Override
                public String getReason() {
                    return "This category has no split options to configure.";
                }
            })
    };
    private final Screen parent;
    private final PracticeCategory category;

    public SettingsTypeSelectionScreen(Screen parent, PracticeCategory category) {
        super(new LiteralText("Select Config Type (" + category.getName(false) + ")"));
        this.parent = parent;
        this.category = category;
    }

    @Override
    public void onClose() {
        if (this.client != null) {
            this.client.openScreen(this.parent);
            return;
        }
        super.onClose();
    }

    @Override
    protected void init() {
        int index = 0;
        int width = (int) (this.width / 2 * 0.8F);
        int height = this.height / 5;
        for (ButtonChoice buttonChoice : this.buttons) {
            ButtonWidget bw = this.addButton(new LimitlessButtonWidget(index % 2 == 0, buttonChoice.icon, null, this.width / 2 - width / 2, this.height * (index + 1) / (this.buttons.length + 1) - height / 2, width, height, buttonChoice.text, b -> {
                if (this.client != null) {
                    Screen screen = buttonChoice.screenTask.execute(this.category);
                    this.client.openScreen(screen);
                }
            }, (button, matrices, mouseX, mouseY) -> {
                if (buttonChoice.disabledTask != null && buttonChoice.disabledTask.isDisabled(this.category)) {
                    this.renderTooltip(matrices, new LiteralText(buttonChoice.disabledTask.getReason()).formatted(Formatting.YELLOW), mouseX, mouseY);
                }
            }));
            if (buttonChoice.disabledTask != null) {
                bw.active = !buttonChoice.disabledTask.isDisabled(this.category);
            }
            index++;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PeepoPractice.drawBackground(matrices, this);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
    }

    private static class ButtonChoice {
        public final Text text;
        public final Identifier icon;
        public final ButtonScreenTask screenTask;
        public ButtonDisabledInfo disabledTask;

        public ButtonChoice(Text text, Identifier icon, ButtonScreenTask screenTask) {
            this.text = text;
            this.icon = icon;
            this.screenTask = screenTask;
        }

        public ButtonChoice(Text text, Identifier icon, ButtonScreenTask screenTask, ButtonDisabledInfo disabledTask) {
            this(text, icon, screenTask);
            this.disabledTask = disabledTask;
        }
    }

    public interface ButtonScreenTask {
        Screen execute(PracticeCategory category);
    }

    public abstract static class ButtonDisabledInfo {
        public abstract boolean isDisabled(PracticeCategory category);
        public abstract String getReason();
    }
}
