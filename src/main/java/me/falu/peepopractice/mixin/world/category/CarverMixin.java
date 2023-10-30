package me.falu.peepopractice.mixin.world.category;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Carver.class)
public abstract class CarverMixin {
    @Shadow
    @Final
    public static Carver<ProbabilityConfig> UNDERWATER_CANYON;

    @Shadow
    public abstract Codec<ConfiguredCarver<ProbabilityConfig>> getCodec();

    @WrapOperation(method = "carveRegion", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1))
    private int peepoPractice$alwaysExposed(int a, int b, Operation<Integer> original) {
        if (PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.RAVINE_ENTER_SPLIT) && this.getCodec() == UNDERWATER_CANYON.getCodec()) {
            return b;
        }
        return original.call(a, b);
    }
}
