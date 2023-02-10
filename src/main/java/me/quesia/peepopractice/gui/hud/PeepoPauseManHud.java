package me.quesia.peepopractice.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class PeepoPauseManHud extends DrawableHelper {
    private static final Identifier PEEPO_PAUSE_MAN = new Identifier(PeepoPractice.MOD_CONTAINER.getMetadata().getId(), "sprite/peepopauseman.png");
    private static final Identifier PEEPO_PAG_MAN = new Identifier(PeepoPractice.MOD_CONTAINER.getMetadata().getId(), "sprite/peepopagman.png");
    private final MinecraftClient client;
    private Long startTime;

    public PeepoPauseManHud(MinecraftClient client) {
        this.client = client;
    }

    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrices) {
        if (PeepoPractice.CATEGORY.hasCustomValue("showPauseBoy") && (boolean) PeepoPractice.CATEGORY.getCustomValue("showPauseBoy")) {
            if (this.startTime == null) {
                this.startTime = System.currentTimeMillis();
            }

            int seconds = 2;
            long time = System.currentTimeMillis();
            long difference = time - this.startTime;
            float div = (float) difference / (seconds * 1000);
            float percentage = (float) MathHelper.clamp(1 - Math.pow(1 - div, 5), 0.0F, 1.0F);
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            Identifier texture = PEEPO_PAUSE_MAN;
            if (PeepoPractice.CATEGORY.hasCustomValue("isCompletion")) {
                if ((boolean) PeepoPractice.CATEGORY.getCustomValue("isCompletion")) {
                    texture = PEEPO_PAG_MAN;
                } else {
                    texture = null;
                }
            }
            if (texture != null) {
                this.client.getTextureManager().bindTexture(texture);
                this.setZOffset(100);
                RenderSystem.translatef(0, 0, 100.0F);
                int spriteDiv = 6;
                int spriteWidth = 112 / spriteDiv;
                int spriteHeight = 84 / spriteDiv;
                drawTexture(matrices, 0, (int) (this.client.getWindow().getScaledHeight() - spriteHeight * percentage), 0.0F, 0.0F, spriteWidth, spriteHeight, spriteWidth, spriteHeight);
            }

            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        } else {
            this.startTime = null;
        }
    }
}
