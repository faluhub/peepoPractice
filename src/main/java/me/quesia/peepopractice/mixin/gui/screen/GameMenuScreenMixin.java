package me.quesia.peepopractice.mixin.gui.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends ScreenMixin {

    private ButtonWidget quitButton;
    private boolean renderTitle = false;
    private final Text replayText = new LiteralText("Replay Split");
    private final Text configureText = new LiteralText("Configure Split");
    private AbstractButtonWidget replayButton;

    @WrapOperation(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 0), slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=menu.returnToMenu")
    ))
    private AbstractButtonWidget peepoPractice$customButtons(GameMenuScreen screen, AbstractButtonWidget abstractButtonWidget, Operation<AbstractButtonWidget> original) {
        assert this.client != null;
        this.renderTitle = !PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY);

        if (PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            return original.call(screen, abstractButtonWidget);
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
            original.call(screen, this.quitButton);
        }

        return this.replayButton = original.call(screen,
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
                                this.client.openScreen(new SettingsTypeSelectionScreen((Screen) (Object) this, PeepoPractice.CATEGORY));
                            }
                        }
                )
        );
    }

    @Override
    protected  <T extends AbstractButtonWidget> void peepoPractice$onButtonAdded(T button, CallbackInfoReturnable<T> cir) {
        if (this.quitButton != null && button.getMessage().getString().equals("menu.quitWorld")) {
            button.setWidth(this.quitButton.getWidth());
            button.x = this.quitButton.x;
            button.y = this.quitButton.y;
            button.setMessage(this.quitButton.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("TAIL"))
    private void peepoPractice$renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
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
