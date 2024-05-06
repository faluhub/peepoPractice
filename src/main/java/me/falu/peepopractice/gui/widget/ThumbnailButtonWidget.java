package me.falu.peepopractice.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.falu.peepopractice.core.category.PracticeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class ThumbnailButtonWidget extends ButtonWidget {
    private static final Identifier GEAR = new Identifier("peepopractice", "textures/gear.png");
    private final PracticeCategory category;
    private final ButtonWidget configButton;

    public ThumbnailButtonWidget(int x, int y, int width, int height, PracticeCategory category, ButtonWidget configButton, PressAction pressAction) {
        super(x, y, width, height, new LiteralText(""), pressAction);
        this.category = category;
        this.configButton = configButton;
    }

    @Override
    public boolean isHovered() {
        return super.isHovered() && !this.configButton.isHovered();
    }

    @Override
    @SuppressWarnings({ "deprecation", "DuplicatedCode" })
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int yOffset = this.getYImage(this.isHovered());

        client.getTextureManager().bindTexture(WIDGETS_LOCATION);

        RenderSystem.pushMatrix();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        this.drawTexture(matrices, this.x, this.y, 0, 46 + yOffset * 20, 3, 3);
        drawTexture(matrices, this.x + 3, this.y, this.width - 6, 3, 3, 46 + yOffset * 20, 1, 3, 256, 256);
        this.drawTexture(matrices, this.x + this.width - 3, this.y, 197, 46 + yOffset * 20, 3, 3);
        drawTexture(matrices, this.x, this.y + 3, 3, this.height - 6, 0, 49 + yOffset * 20, 3, 1, 256, 256);
        this.drawTexture(matrices, this.x, this.y + this.height - 3, 0, 46 + 17 + yOffset * 20, 3, 3);
        drawTexture(matrices, this.x + 3, this.y + this.height - 3, this.width - 6, 3, 3, 46 + 17 + yOffset * 20, 1, 3, 256, 256);
        this.drawTexture(matrices, this.x + this.width - 3, this.y + this.height - 3, 197, 46 + 17 + yOffset * 20, 3, 3);
        drawTexture(matrices, this.x + this.width - 3, this.y + 3, 3, this.height - 6, 197, 49 + yOffset * 20, 3, 1, 256, 256);

        client.getTextureManager().bindTexture(new Identifier("peepopractice", "textures/category/" + this.category.getId() + ".png"));
        drawTexture(matrices, this.x + 3, this.y + 3, 0.0F, 0.0F, this.width - 6, this.height - 6, this.width - 6, this.height - 6);

        if (this.isHovered()) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
        RenderSystem.popMatrix();

        this.configButton.render(matrices, mouseX, mouseY, delta);
        client.getTextureManager().bindTexture(GEAR);
        drawTexture(matrices, this.configButton.x + 2, this.configButton.y + 2, 0.0F, 0.0F, 16, 16, 16, 16);

        fill(matrices, this.x, this.y + this.height, this.x + this.width, this.y + this.height - this.height / 2 + 5, BackgroundHelper.ColorMixer.getArgb(150, 0, 0, 0));
        this.drawCenteredString(
                matrices,
                client.textRenderer,
                this.category.getName(false),
                this.x + this.width / 2,
                this.y + this.height - this.height / 4 - client.textRenderer.fontHeight - 2 + 4,
                0xFFFFFF
        );
        this.drawCenteredText(
                matrices,
                client.textRenderer,
                this.isHovered() ? this.category.getStatsText() : this.category.getPbText(),
                this.x + this.width / 2,
                this.y + this.height - this.height / 4 + 4,
                0xFFFFFF
        );
    }
}
