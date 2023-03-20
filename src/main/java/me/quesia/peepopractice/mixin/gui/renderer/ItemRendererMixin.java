package me.quesia.peepopractice.mixin.gui.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow public float zOffset;

    @WrapOperation(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;scalef(FFF)V", ordinal = 1))
    private void peepoPractice$scaleDown(float x, float y, float z, Operation<Void> original, ItemStack stack, int x_, int y_, BakedModel model) {
        if (stack.getTag() != null && stack.getTag().getBoolean("IsDisplayItem")) {
            original.call(8.0F, 8.0F, 8.0F);
            return;
        }
        original.call(x, y, z);
    }

    @ModifyArg(method = "renderGuiItemModel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;translatef(FFF)V", ordinal = 1), index = 2)
    private float peepoPractice$bringForward(float z, @Local(argsOnly = true) ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().getBoolean("IsDisplayItem")) {
            return this.zOffset;
        }
        return z;
    }
}
