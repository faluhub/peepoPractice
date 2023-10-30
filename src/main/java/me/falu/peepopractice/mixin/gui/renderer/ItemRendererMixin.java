package me.falu.peepopractice.mixin.gui.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    public float zOffset;

    @Unique
    private static boolean shouldScale(ItemStack stack) {
        return stack.getTag() != null && (stack.getTag().contains("MaxCount") || stack.getTag().contains("MinCount"));
    }

    @ModifyArg(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;translatef(FFF)V", ordinal = 0), index = 2)
    private float peepoPractice$bringForward(float z, @Local(argsOnly = true) ItemStack stack) {
        if (shouldScale(stack)) {
            return this.zOffset;
        }
        return z;
    }
}
