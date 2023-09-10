package me.falu.peepopractice.mixin.gui.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.gui.screen.CategorySelectionScreen;
import me.falu.peepopractice.owner.GenerationShutdownOwner;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelLoadingScreen.class, priority = 995)
public abstract class LevelLoadingScreenMixin extends Screen {
    @Unique private static final Identifier WIDE_PEEPO_HAPPY = new Identifier(PeepoPractice.MOD_ID, "textures/widepeepohappy.png");

    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            this.addButton(
                    new ButtonWidget(
                            this.width / 2 - 60,
                            this.height / 2 - 80,
                            120,
                            20,
                            new LiteralText("Cancel"),
                            button -> {
                                button.active = false;
                                if (this.client != null) {
                                    if (this.client.getServer() != null) {
                                        ((GenerationShutdownOwner) this.client.getServer()).peepopractice$shutdown();
                                        this.client.disconnect();
                                        this.client.openScreen(new CategorySelectionScreen(null));
                                    }
                                }
                            }
                    )
            );
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;drawChunkMap(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/gui/WorldGenerationProgressTracker;IIII)V"))
    private void peepoPractice$noChunkMap(MatrixStack matrices, WorldGenerationProgressTracker worldGenerationProgressTracker, int i, int j, int k, int l, Operation<Void> original) {
        if (PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            original.call(matrices, worldGenerationProgressTracker, i, j, k, l);
            return;
        }
        if (this.client == null) { return; }
        this.client.getTextureManager().bindTexture(WIDE_PEEPO_HAPPY);
        int percentage = Math.min(worldGenerationProgressTracker.getProgressPercentage(), 100);
        int textureHeight = 75 / 2;
        int initialTextureWidth = 125;
        int maxTextureWidth = 320 - initialTextureWidth + this.width / 4;
        int textureWidth = (int) ((initialTextureWidth + (int) (maxTextureWidth * (percentage / 100.0F))) / 2.0F);
        drawTexture(matrices, this.width / 2 - textureWidth / 2, this.height / 2 - textureHeight / 2 - this.textRenderer.fontHeight, 0.0F, 0.0F, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    @SuppressWarnings("deprecation")
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;drawCenteredString(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void peepoPractice$textPosition(LevelLoadingScreen screen, MatrixStack matrices, TextRenderer textRenderer, String s, int i, int j, int k, Operation<Void> original) {
        if (PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            original.call(screen, matrices, textRenderer, s, i, j, k);
            return;
        }
        RenderSystem.pushMatrix();
        float scale = 2.0F;
        RenderSystem.scalef(scale, scale, 1.0F);
        this.drawCenteredString(matrices, textRenderer, s, (int) (i / scale), (int) ((this.height / 2 + textRenderer.fontHeight * scale) / scale), k);
        RenderSystem.popMatrix();
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;renderBackground(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void peepoPractice$customBackground(LevelLoadingScreen screen, MatrixStack matrices, Operation<Void> original) {
        if (PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            original.call(screen, matrices);
            return;
        }
        PeepoPractice.drawBackground(matrices, this);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void peepoPractice$renderButtons(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        for (AbstractButtonWidget button : this.buttons) {
            button.render(matrices, mouseX, mouseY, delta);
        }
    }
}
