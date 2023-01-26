package me.quesia.peepopractice.mixin.gui.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow public float zOffset;
    private ItemStack stack;

    @SuppressWarnings("UnusedDeclaration")
    @Inject(method = "renderGuiItemModel", at = @At("HEAD"))
    private void captureStack(ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
        this.stack = stack;
    }

    @SuppressWarnings({ "deprecation", "UnusedDeclaration" })
    @Redirect(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;scalef(FFF)V", ordinal = 1))
    private void scaleDown(float x, float y, float z) {
        if (this.stack.getTag() != null && this.stack.getTag().getBoolean("IsDisplayItem")) {
            RenderSystem.scalef(8.0F, 8.0F, 8.0F);
            return;
        }
        RenderSystem.scalef(x, y, z);
    }

    @SuppressWarnings({ "deprecation", "UnusedDeclaration" })
    @Redirect(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;translatef(FFF)V", ordinal = 1))
    private void bringForward(float x, float y, float z) {
        if (this.stack.getTag() != null && this.stack.getTag().getBoolean("IsDisplayItem")) {
            RenderSystem.translatef(x, y, this.zOffset);
            return;
        }
        RenderSystem.translatef(x, y, z);
    }
}
