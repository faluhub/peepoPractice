package me.quesia.peepopractice.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class OddLimitlessButtonWidget extends ButtonWidget {
    public final boolean odd;
    public final Identifier icon;

    public OddLimitlessButtonWidget(boolean odd, Identifier icon, int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
        super(x, y, width, height, message, onPress);
        this.odd = odd;
        this.icon = icon;
    }

    @SuppressWarnings("deprecation")
    @Override
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
        DrawableHelper.drawTexture(matrices, this.x + 3, this.y, this.width - 6, 3, 3, 46 + i * 20, 1, 3, 256, 256);
        this.drawTexture(matrices, this.x + this.width - 3, this.y, 197, 46 + i * 20, 3, 3);
        DrawableHelper.drawTexture(matrices, this.x, this.y + 3, 3, this.height - 6, 0, 49 + i * 20, 3, 1, 256, 256);
        this.drawTexture(matrices, this.x, this.y + this.height - 3, 0, 46 + 17 + i * 20, 3, 3);
        DrawableHelper.drawTexture(matrices, this.x + 3, this.y + this.height - 3, this.width - 6, 3, 3, 46 + 17 + i * 20, 1, 3, 256, 256);
        this.drawTexture(matrices, this.x + this.width - 3, this.y + this.height - 3, 197, 46 + 17 + i * 20, 3, 3);
        DrawableHelper.drawTexture(matrices, this.x + this.width - 3, this.y + 3, 3, this.height - 6, 197, 49 + i * 20, 3, 1, 256, 256);
        DrawableHelper.fill(matrices, this.x + 3, this.y + 3, this.x + this.width - 3, this.y + this.height - 3, this.active ? LimitlessButtonWidget.BG_COLOR : LimitlessButtonWidget.BG_INACTIVE_COLOR);

        int j = this.active ? 0xFFFFFF : 0xA0A0A0;
        int textureSize = 32;
        int textWidth = textRenderer.getWidth(this.getMessage().getString());
        RenderSystem.pushMatrix();
        float colour = 0.3F;
        if (this.odd) {
            textRenderer.draw(this.getMessage().getString(), this.x + this.width / 4.0F - this.width / 8.0F, this.y + this.height / 2.0F - textRenderer.fontHeight / 4.0F, j | MathHelper.ceil(this.alpha * 255.0F) << 24, matrices.peek().getModel(), true, false);
            if (!this.active) {
                RenderSystem.color3f(colour, colour, colour);
            }
            MinecraftClient.getInstance().getTextureManager().bindTexture(this.icon);
            drawTexture(matrices, this.x + this.width - this.width / 4, this.y + this.height / 4, 0.0F, 0.0F, textureSize, textureSize, textureSize, textureSize);
        } else {
            textRenderer.draw(this.getMessage().getString(), this.x + this.width - textWidth - this.width / 8.0F, this.y + this.height / 2.0F - textRenderer.fontHeight / 4.0F, j | MathHelper.ceil(this.alpha * 255.0F) << 24, matrices.peek().getModel(), true, true);
            MinecraftClient.getInstance().getTextureManager().bindTexture(this.icon);
            if (!this.active) {
                RenderSystem.color3f(colour, colour, colour);
            }
            drawTexture(matrices, this.x + this.width / 4 - textureSize, this.y + this.height / 4, 0.0F, 0.0F, textureSize, textureSize, textureSize, textureSize);
        }
        RenderSystem.popMatrix();

        if (this.isHovered() && this.active) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
    }
}
