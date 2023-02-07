package me.quesia.peepopractice.mixin.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

@Mixin(SubtitlesHud.class)
public class SubtitlesHudMixin {
    @Shadow private boolean enabled;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private List<SubtitlesHud.SubtitleEntry> entries;

    /**
     * @author Quesia
     * @reason fixes crash.
     */
    @Overwrite
    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrixStack) {
        if (this.client.player == null) { return; }
        if (!this.enabled && this.client.options.showSubtitles) {
            this.client.getSoundManager().registerListener((SoundInstanceListener) this);
            this.enabled = true;
        } else if (this.enabled && !this.client.options.showSubtitles) {
            this.client.getSoundManager().unregisterListener((SoundInstanceListener) this);
            this.enabled = false;
        }
        if (!this.enabled || this.entries.isEmpty()) {
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vec3d vec3d = new Vec3d(this.client.player.getX(), this.client.player.getEyeY(), this.client.player.getZ());
        Vec3d vec3d2 = new Vec3d(0.0, 0.0, -1.0).rotateX(-this.client.player.pitch * ((float)Math.PI / 180)).rotateY(-this.client.player.yaw * ((float)Math.PI / 180));
        Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0).rotateX(-this.client.player.pitch * ((float)Math.PI / 180)).rotateY(-this.client.player.yaw * ((float)Math.PI / 180));
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3);
        int i = 0;
        int j = 0;
        Iterator<SubtitlesHud.SubtitleEntry> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            SubtitlesHud.SubtitleEntry subtitleEntry = iterator.next();
            if (subtitleEntry.getTime() + 3000L <= Util.getMeasuringTimeMs()) {
                iterator.remove();
                continue;
            }
            j = Math.max(j, this.client.textRenderer.getWidth(subtitleEntry.getText()));
        }
        j += this.client.textRenderer.getWidth("<") + this.client.textRenderer.getWidth(" ") + this.client.textRenderer.getWidth(">") + this.client.textRenderer.getWidth(" ");
        try {
            for (SubtitlesHud.SubtitleEntry subtitleEntry : this.entries) {
                Text text = subtitleEntry.getText();
                Vec3d vec3d5 = subtitleEntry.getPosition().subtract(vec3d).normalize();
                double d = -vec3d4.dotProduct(vec3d5);
                double e = -vec3d2.dotProduct(vec3d5);
                boolean bl = e > 0.5;
                int l = j / 2;
                int m = this.client.textRenderer.fontHeight;
                int n = m / 2;
                int o = this.client.textRenderer.getWidth(text);
                int p = MathHelper.floor(MathHelper.clampedLerp(255.0, 75.0, (float)(Util.getMeasuringTimeMs() - subtitleEntry.getTime()) / 3000.0f));
                int q = p << 16 | p << 8 | p;
                RenderSystem.pushMatrix();
                RenderSystem.translatef((float)this.client.getWindow().getScaledWidth() - (float) l - 2.0f, (float)(this.client.getWindow().getScaledHeight() - 30) - (float) (i * (m + 1)), 0.0f);
                RenderSystem.scalef(1.0f, 1.0f, 1.0f);
                SubtitlesHud.fill(matrixStack, -l - 1, -n - 1, l + 1, n + 1, this.client.options.getTextBackgroundColor(0.8f));
                RenderSystem.enableBlend();
                if (!bl) {
                    if (d > 0.0) {
                        this.client.textRenderer.draw(matrixStack, ">", (float)(l - this.client.textRenderer.getWidth(">")), (float)(-n), q - 16777216);
                    } else if (d < 0.0) {
                        this.client.textRenderer.draw(matrixStack, "<", (float)(-l), (float)(-n), q - 16777216);
                    }
                }
                this.client.textRenderer.draw(matrixStack, text, (float)(-o / 2), (float)(-n), q - 16777216);
                RenderSystem.popMatrix();
                ++i;
            }
        } catch (ConcurrentModificationException ignored) {}
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
}
