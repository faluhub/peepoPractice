package me.quesia.peepopractice.mixin.structure.category;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategoryUtils;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(BastionRemnantFeatureConfig.class)
public class BastionRemnantFeatureConfigMixin {
    @Shadow @Final private List<StructurePoolFeatureConfig> possibleConfigs;

    @Inject(method = "getRandom", at = @At("RETURN"), cancellable = true)
    private void bastionType(Random random, CallbackInfoReturnable<StructurePoolFeatureConfig> cir) {
        if (PeepoPractice.CATEGORY == PracticeCategories.NETHER_SPLIT) {
            StructurePoolFeatureConfig value = cir.getReturnValue(); // change to BastionType.id value for testing
            PeepoPractice.CATEGORY.putCustomValue("bastionType", this.possibleConfigs.indexOf(value));
            cir.setReturnValue(value);
        }
    }
}
