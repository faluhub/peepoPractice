package me.falu.peepopractice.mixin.world.category;

import com.llamalad7.mixinextras.sugar.Local;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(RuleStructureProcessor.class)
public class RuleStructureProcessorMixin {
    @Redirect(method = "process", at = @At(value = "NEW", target = "(J)Ljava/util/Random;"))
    private Random fixSameBastion(long seed, @Local(ordinal = 0, argsOnly = true) BlockPos p1, @Local(ordinal = 1, argsOnly = true) BlockPos p2) {
        return PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.BASTION_SPLIT) ? new Random() : new Random(seed);
    }
}
