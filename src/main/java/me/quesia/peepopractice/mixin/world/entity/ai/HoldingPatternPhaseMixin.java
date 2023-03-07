package me.quesia.peepopractice.mixin.world.entity.ai;

import me.quesia.peepopractice.core.category.CategoryPreference;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(HoldingPatternPhase.class)
public abstract class HoldingPatternPhaseMixin extends AbstractPhase {
    public HoldingPatternPhaseMixin(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Redirect(method = "method_6841", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 3))
    private int oneInEight(Random instance, int bound) {
        if (CategoryPreference.getBoolValue("one_in_eight")) { return 0; }
        return instance.nextInt(bound);
    }

    @Redirect(method = "method_6842", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F"))
    private float redirectTargetHeight(Random instance) {
        if (CategoryPreference.getBoolValue("no_early_flyaway")) { return 0.0F; }
        return instance.nextFloat();
    }
}
