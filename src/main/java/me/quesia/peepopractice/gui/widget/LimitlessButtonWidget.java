package me.quesia.peepopractice.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class LimitlessButtonWidget extends ButtonWidget {
    public static final int BG_INACTIVE_COLOR;
    public static final int BG_COLOR = BG_INACTIVE_COLOR = BackgroundHelper.ColorMixer.getArgb(60, 0, 0, 0);
    private final Identifier icon;

    public LimitlessButtonWidget(Identifier icon, int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
        super(x, y, width, height, message, onPress);
        this.icon = icon;
    }

    public LimitlessButtonWidget(Identifier icon, int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
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
        DrawableHelper.fill(matrices, this.x + 3, this.y + 3, this.x + this.width - 3, this.y + this.height - 3, this.active ? BG_COLOR : BG_INACTIVE_COLOR);

        int j = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0f) << 24);

        if (this.isHovered() && this.active) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
    }
}
