package me.falu.peepopractice.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.falu.peepopractice.PeepoPractice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

public class LimitlessButtonWidget extends ButtonWidget {
    public static final int BG_INACTIVE_COLOR;
    public static final int BG_COLOR = BG_INACTIVE_COLOR = PeepoPractice.BACKGROUND_OVERLAY_COLOUR;
    public final Boolean odd;
    public final Identifier icon;
    public final Integer textureSize;

    public LimitlessButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
        this(null, null, null, x, y, width, height, message, onPress);
    }

    public LimitlessButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, TooltipSupplier tooltipSupplier) {
        this(null, null, null, x, y, width, height, message, onPress, tooltipSupplier);
    }

    public LimitlessButtonWidget(@Nullable Boolean odd, @Nullable Identifier icon, @Nullable Integer textureSize, int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
        super(x, y, width, height, message, onPress);
        this.odd = odd;
        this.icon = icon;
        this.textureSize = textureSize != null ? textureSize : 32;
    }

    public LimitlessButtonWidget(@Nullable Boolean odd, @Nullable Identifier icon, @Nullable Integer textureSize, int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
        this.odd = odd;
        this.icon = icon;
        this.textureSize = textureSize != null ? textureSize : 32;
    }

    private void drawText(String[] parts, float x, float y, int color, Matrix4f matrix, boolean mirror) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int i = 0;
        int spacing = textRenderer.fontHeight + 2;
        for (String part : parts) {
            int textWidth = textRenderer.getWidth(part);
            textRenderer.draw(part, (i != 0 ? this.x + this.width / 2.0F - textWidth / 2.0F : x), y + spacing * i, color, matrix, true, mirror);
            i++;
        }
    }

    @Override
    @SuppressWarnings({ "deprecation", "DuplicatedCode" })
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
        fill(matrices, this.x + 3, this.y + 3, this.x + this.width - 3, this.y + this.height - 3, PeepoPractice.BACKGROUND_OVERLAY_COLOUR);

        int j = this.active ? 0xFFFFFF : 0xA0A0A0;
        RenderSystem.pushMatrix();
        float colour = 0.3F;
        String[] parts = this.getMessage().getString().split("\n");
        int textWidth = textRenderer.getWidth(parts[0]);
        if (this.odd != null) {
            if (this.odd) {
                this.drawText(parts, this.x + this.width / 4.0F - this.width / 8.0F, this.y + this.height / 2.0F - textRenderer.fontHeight / 4.0F, j | MathHelper.ceil(this.alpha * 255.0F) << 24, matrices.peek().getModel(), false);
                if (this.icon != null) {
                    if (!this.active) {
                        RenderSystem.color3f(colour, colour, colour);
                    }
                    MinecraftClient.getInstance().getTextureManager().bindTexture(this.icon);
                    RenderSystem.translatef(0.0F, 0.0F, 10.0F);
                    drawTexture(matrices, this.x + this.width - this.width / 4, this.y + this.height / 2 - this.textureSize / 2, 0.0F, 0.0F, this.textureSize, this.textureSize, this.textureSize, this.textureSize);
                }
            } else {
                this.drawText(parts, this.x + this.width - textWidth - this.width / 8.0F, this.y + this.height / 2.0F - textRenderer.fontHeight / 4.0F, j | MathHelper.ceil(this.alpha * 255.0F) << 24, matrices.peek().getModel(), true);
                if (this.icon != null) {
                    if (!this.active) {
                        RenderSystem.color3f(colour, colour, colour);
                    }
                    MinecraftClient.getInstance().getTextureManager().bindTexture(this.icon);
                    RenderSystem.translatef(0.0F, 0.0F, 10.0F);
                    drawTexture(matrices, this.x + this.width / 4 - this.textureSize, this.y + this.height / 2 - this.textureSize / 2, 0.0F, 0.0F, this.textureSize, this.textureSize, this.textureSize, this.textureSize);
                }
            }
        } else {
            if (this.icon != null) {
                this.drawText(parts, this.x + this.width / 2.0F - textWidth / 2.0F, this.y + this.height / 6.0F - textRenderer.fontHeight / 4.0F, j | MathHelper.ceil(this.alpha * 255.0F) << 24, matrices.peek().getModel(), false);
                if (!this.active) {
                    RenderSystem.color3f(colour, colour, colour);
                }
                MinecraftClient.getInstance().getTextureManager().bindTexture(this.icon);
                RenderSystem.translatef(0.0F, 0.0F, -100.0F);
                this.setZOffset(-100);
                drawTexture(matrices, this.x + this.width / 2 - this.textureSize / 2, this.y + this.height / 4 + this.textureSize, 0.0F, 0.0F, this.textureSize, this.textureSize, this.textureSize, this.textureSize);
            } else {
                this.drawText(parts, this.x + this.width / 2.0F - textWidth / 2.0F, this.y + this.height / 2.0F - textRenderer.fontHeight / 2.0F, j | MathHelper.ceil(this.alpha * 255.0F) << 24, matrices.peek().getModel(), false);
            }
        }
        RenderSystem.popMatrix();

        if (this.isHovered()) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
    }
}
