package me.quesia.peepopractice.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SettingsTypeSelectionScreen extends Screen {
    private final ButtonChoice[] buttons = new ButtonChoice[] {
            new ButtonChoice(new LiteralText("Inventory"), new Identifier("textures/item/chest_minecart.png"), category -> EditInventoryScreen.create(this, category)),
            new ButtonChoice(new LiteralText("Preferences"), new Identifier("textures/item/heart_of_the_sea.png"), category -> new CategoryPreferencesScreen(this, category), PracticeCategory::hasPreferences),
            new ButtonChoice(new LiteralText("Standard Settings"), new Identifier(PeepoPractice.MOD_CONTAINER.getMetadata().getId(), "icon/gear.png"), category -> new StandardSettingsScreen(this, category), (category) -> !PeepoPractice.HAS_STANDARD_SETTINGS),
            new ButtonChoice(new LiteralText("Split"), new Identifier("textures/item/clock_00.png"), category -> new SplitSettingsScreen(this, category), PracticeCategory::hasSplitEvent)
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
        int width = (int) (this.width / 2 * .8);
        int height = this.height / 5;
        for (ButtonChoice buttonChoice : this.buttons) {
            ButtonWidget bw = this.addButton(new LimitlessButtonWidget(index % 2 == 0, buttonChoice.icon, null, this.width / 2 - width / 2, this.height * (index + 1) / (this.buttons.length + 1) - height / 2, width, height, buttonChoice.text, b -> {
                if (this.client != null) {
                    Screen screen = buttonChoice.screenTask.execute(this.category);
                    this.client.openScreen(screen);
                }
            }));
            if (buttonChoice.disabledTask != null) {
                bw.active = buttonChoice.disabledTask.execute(this.category);
            }
            index++;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
    }

    private static class ButtonChoice {
        public final Text text;
        public final Identifier icon;
        public final ButtonScreenTask screenTask;
        public ButtonDisabledTask disabledTask;

        public ButtonChoice(Text text, Identifier icon, ButtonScreenTask screenTask) {
            this.text = text;
            this.icon = icon;
            this.screenTask = screenTask;
        }

        public ButtonChoice(Text text, Identifier icon, ButtonScreenTask screenTask, ButtonDisabledTask disabledTask) {
            this(text, icon, screenTask);
            this.disabledTask = disabledTask;
        }
    }

    public interface ButtonScreenTask {
        Screen execute(PracticeCategory category);
    }

    public interface ButtonDisabledTask {
        boolean execute(PracticeCategory category);
    }
}
