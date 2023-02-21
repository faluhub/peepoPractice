package me.quesia.peepopractice.mixin.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelLoadingScreen.class, priority = 995)
public abstract class LevelLoadingScreenMixin extends Screen {
    private static final Identifier WIDE_PEEPO_HAPPY = new Identifier(PeepoPractice.MOD_ID, "sprite/widepeepohappy.png");

    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void preventWorldPreview(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        PeepoPractice.disableWorldPreview();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;drawChunkMap(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/gui/WorldGenerationProgressTracker;IIII)V"))
    private void noChunkMap(MatrixStack matrices, WorldGenerationProgressTracker worldGenerationProgressTracker, int i, int j, int k, int l) {
        if (PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            LevelLoadingScreen.drawChunkMap(matrices, worldGenerationProgressTracker, i, j, k, l);
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
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;drawCenteredString(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void textPosition(LevelLoadingScreen instance, MatrixStack matrices, TextRenderer textRenderer, String s, int i, int j, int k) {
        if (PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            instance.drawCenteredString(matrices, textRenderer, s, i, j, k);
            return;
        }

        RenderSystem.pushMatrix();
        float scale = 2.0F;
        RenderSystem.scalef(scale, scale, 1.0F);
        instance.drawCenteredString(matrices, textRenderer, s, (int) (i / scale), (int) ((this.height / 2 + textRenderer.fontHeight * scale) / scale), k);
        RenderSystem.popMatrix();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;renderBackground(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.BY, by = 1))
    private void customBackground(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            this.renderBackground(matrices);
            return;
        }
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
    }
}
