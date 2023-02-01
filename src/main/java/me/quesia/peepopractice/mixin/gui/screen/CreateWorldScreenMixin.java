package me.quesia.peepopractice.mixin.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategoryUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow private Difficulty field_24289;
    @Shadow private Difficulty field_24290;
    @Shadow private boolean cheatsEnabled;
    @Shadow private boolean tweakedCheats;
    private boolean renderTitle = false;

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void setDefaultDifficulty(CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            this.field_24289 = this.field_24290 = Difficulty.EASY;
            this.cheatsEnabled = true;
            this.tweakedCheats = true;
            this.renderTitle = true;
        }
    }

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void checkDisconnected(CallbackInfo ci) {
        if (this.client != null && this.client.isIntegratedServerRunning()) {
            PracticeCategoryUtils.quit();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/StringRenderable;III)V", ordinal = 0))
    private void cancelTitleRender(CreateWorldScreen instance, MatrixStack matrixStack, TextRenderer textRenderer, StringRenderable stringRenderable, int i, int j, int k) {
        if (!this.renderTitle) {
            this.drawCenteredText(matrixStack, textRenderer, stringRenderable, i, j, k);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    @SuppressWarnings("deprecation")
    private void renderCategoryName(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.renderTitle) {
            RenderSystem.pushMatrix();
            RenderSystem.scalef(2.0F, 2.0F, 2.0F);
            this.drawCenteredText(matrices, this.textRenderer, new LiteralText(PeepoPractice.CATEGORY.getName()), this.width / 2 / 2, 6, 0xFFFFFF);
            RenderSystem.popMatrix();
        }
    }
}
