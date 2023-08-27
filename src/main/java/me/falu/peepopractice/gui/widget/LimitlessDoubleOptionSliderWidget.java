package me.falu.peepopractice.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.falu.peepopractice.PeepoPractice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class LimitlessDoubleOptionSliderWidget extends DoubleOptionSliderWidget {
    public LimitlessDoubleOptionSliderWidget(GameOptions gameOptions, int x, int y, int width, int height, DoubleOption option) {
        super(gameOptions, x, y, width, height, option);
    }

    @Override
    @SuppressWarnings({ "deprecation" })
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        minecraftClient.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, 3, 3);
        drawTexture(matrices, this.x + 3, this.y, this.width - 6, 3, 3, 46 + i * 20, 1, 3, 256, 256);
        this.drawTexture(matrices, this.x + this.width - 3, this.y, 197, 46 + i * 20, 3, 3);
        drawTexture(matrices, this.x, this.y + 3, 3, this.height - 6, 0, 49 + i * 20, 3, 1, 256, 256);
        this.drawTexture(matrices, this.x, this.y + this.height - 3, 0, 46 + 17 + i * 20, 3, 3);
        drawTexture(matrices, this.x + 3, this.y + this.height - 3, this.width - 6, 3, 3, 46 + 17 + i * 20, 1, 3, 256, 256);
        this.drawTexture(matrices, this.x + this.width - 3, this.y + this.height - 3, 197, 46 + 17 + i * 20, 3, 3);
        drawTexture(matrices, this.x + this.width - 3, this.y + 3, 3, this.height - 6, 197, 49 + i * 20, 3, 1, 256, 256);
        fill(matrices, this.x + 3, this.y + 3, this.x + this.width - 3, this.y + this.height - 3, PeepoPractice.BACKGROUND_OVERLAY_COLOR);
        this.renderBg(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void renderBg(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        client.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int i = (this.isHovered() ? 2 : 1) * 20;
        int dx = this.x + (int) (this.value * (double) (this.width - 8));
        drawTexture(matrices, dx, this.y, 4, 4, 0, 46 + i, 4, 4, 256, 256);
        drawTexture(matrices, dx + 4, this.y, 4, 4, 196, 46 + i, 4, 4, 256, 256);
        drawTexture(matrices, dx, this.y + this.height - 4, 4, 4, 0, 46 + i + 16, 4, 4, 256, 256);
        drawTexture(matrices, dx + 4, this.y + this.height - 4, 4, 4, 196, 46 + i + 16, 4, 4, 256, 256);
        drawTexture(matrices, dx, this.y + 4, 4, this.height - 8, 0, 46 + i + 4, 4, 1, 256, 256);
        drawTexture(matrices, dx + 4, this.y + 4, 4, this.height - 8, 196, 46 + i + 4, 4, 1, 256, 256);
    }
}
