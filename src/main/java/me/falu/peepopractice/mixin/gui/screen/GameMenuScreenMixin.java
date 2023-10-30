package me.falu.peepopractice.mixin.gui.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.utils.InventoryUtils;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import me.falu.peepopractice.gui.screen.SettingsTypeSelectionScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends ScreenMixin {
    @Unique
    private final Text replayText = new TranslatableText("peepopractice.button.replay_split");
    @Unique
    private final Text configureText = new TranslatableText("peepopractice.button.configure_split");
    @Unique
    private ButtonWidget quitButton;
    @Unique
    private boolean renderTitle = false;
    @Unique
    private AbstractButtonWidget replayButton;
    @Unique
    private AbstractButtonWidget nextButton;
    @Unique
    private boolean hasNextCategory;

    @WrapOperation(
            method = "initWidgets",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=menu.returnToMenu"
                    )
            )
    )
    private AbstractButtonWidget peepoPractice$customButtons(GameMenuScreen screen, AbstractButtonWidget abstractButtonWidget, Operation<AbstractButtonWidget> original) {
        assert this.client != null;
        this.renderTitle = !PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY);

        if (PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            return original.call(screen, abstractButtonWidget);
        }

        int i = -16;

        this.quitButton = new ButtonWidget(
                this.width / 2 - 102,
                this.height / 4 + 120 + i,
                98,
                20,
                new TranslatableText("peepopractice.button.short_save_quit"),
                b -> {
                    b.active = false;
                    PracticeCategoryUtils.quit(true);
                }
        );

        if (!PeepoPractice.HAS_FAST_RESET) {
            original.call(screen, this.quitButton);
        }

        this.replayButton = original.call(
                screen,
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
        this.nextButton = original.call(
                screen,
                new ButtonWidget(
                        this.width / 2 + 4,
                        this.height / 4 + 144 + i,
                        98,
                        20,
                        new TranslatableText("peepopractice.button.next_split"),
                        b -> {
                            b.active = false;
                            PracticeCategory nextCategory = PeepoPractice.getNextCategory();
                            if (nextCategory != null && (FabricLoader.getInstance().isDevelopmentEnvironment() || InGameTimer.getInstance().isCompleted())) {
                                InventoryUtils.saveCurrentPlayerInventory();
                                PeepoPractice.CATEGORY = nextCategory;
                                this.client.openScreen(new CreateWorldScreen(null));
                            }
                        }
                )
        );
        this.nextButton.visible = FabricLoader.getInstance().isDevelopmentEnvironment() || PeepoPractice.CATEGORY.hasCustomValue("isCompletion") && (Boolean) PeepoPractice.CATEGORY.getCustomValue("isCompletion");
        this.hasNextCategory = PeepoPractice.hasNextCategory();
        this.nextButton.active = this.hasNextCategory;

        return this.replayButton;
    }

    @Override
    protected <T extends AbstractButtonWidget> void peepoPractice$onButtonAdded(T button, CallbackInfoReturnable<T> cir) {
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
            if (Screen.hasShiftDown()) {
                this.replayButton.setMessage(this.configureText);
            } else {
                this.replayButton.setMessage(this.replayText);
            }
        }
        if (this.nextButton != null) {
            this.nextButton.visible = FabricLoader.getInstance().isDevelopmentEnvironment() || PeepoPractice.CATEGORY.hasCustomValue("isCompletion") && (Boolean) PeepoPractice.CATEGORY.getCustomValue("isCompletion");
            if (this.nextButton.visible) {
                this.nextButton.active = this.hasNextCategory;
            }
        }

        if (this.renderTitle) {
            RenderSystem.pushMatrix();
            RenderSystem.scalef(2.0F, 2.0F, 2.0F);
            this.drawCenteredText(matrices, this.textRenderer, new LiteralText(PeepoPractice.CATEGORY.getName(true)), this.width / 2 / 2, 6, 0xFFFFFF);
            RenderSystem.popMatrix();
        }
    }
}
