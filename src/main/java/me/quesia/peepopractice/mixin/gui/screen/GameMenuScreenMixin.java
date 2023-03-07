package me.quesia.peepopractice.mixin.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.utils.PracticeCategoryUtils;
import me.quesia.peepopractice.gui.screen.SettingsTypeSelectionScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    private ButtonWidget quitButton;
    private boolean renderTitle = false;
    private final Text replayText = new LiteralText("Replay Split");
    private final Text configureText = new LiteralText("Configure Split");
    private ButtonWidget replayButton;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 7))
    private AbstractButtonWidget customButtons(GameMenuScreen instance, AbstractButtonWidget abstractButtonWidget) {
        if (this.client == null) { return null; }
        this.renderTitle = !PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY);

        if (PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            if (abstractButtonWidget instanceof ButtonWidget) {
                ButtonWidget buttonWidget = (ButtonWidget) abstractButtonWidget;
                return this.addButton(buttonWidget);
            }
        }

        int i = -16;

        this.quitButton = new ButtonWidget(
                this.width / 2 - 102,
                this.height / 4 + 120 + i,
                98,
                20,
                new LiteralText("Save & Quit"),
                b -> {
                    b.active = false;
                    PracticeCategoryUtils.quit(true);
                }
        );

        if (!PeepoPractice.HAS_FAST_RESET) {
            this.addButton(this.quitButton);
        }

        return this.replayButton = this.addButton(
                new ButtonWidget(
                        this.width / 2 + 4,
                        this.height / 4 + 120 + i,
                        98,
                        20,
                        this.replayText,
                        b -> {
                            b.active = false;
                            if (b.getMessage().equals(this.replayText)) {
                                this.client.openScreen(new CreateWorldScreen(null));
                            } else {
                                this.client.openScreen(new SettingsTypeSelectionScreen(this, PeepoPractice.CATEGORY));
                            }
                        }
                )
        );
    }

    @Override
    protected <T extends AbstractButtonWidget> T addButton(T button) {
        if (this.quitButton != null && button.getMessage().getString().equals("menu.quitWorld")) {
            button.setWidth(this.quitButton.getWidth());
            button.x = this.quitButton.x;
            button.y = this.quitButton.y;
            button.setMessage(this.quitButton.getMessage());
            return super.addButton(button);
        }
        return super.addButton(button);
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("TAIL"))
    private void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.replayButton != null && this.replayButton.active) {
            if (Screen.hasShiftDown()) { this.replayButton.setMessage(this.configureText); }
            else { this.replayButton.setMessage(this.replayText); }
        }

        if (this.renderTitle) {
            RenderSystem.pushMatrix();
            RenderSystem.scalef(2.0F, 2.0F, 2.0F);
            this.drawCenteredText(matrices, this.textRenderer, new LiteralText(PeepoPractice.CATEGORY.getName(true)), this.width / 2 / 2, 6, 0xFFFFFF);
            RenderSystem.popMatrix();
        }
    }
}
