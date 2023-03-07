package me.quesia.peepopractice.mixin.structure.category;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.CategoryPreference;
import me.quesia.peepopractice.core.category.PracticeTypes;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
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
        if (PeepoPractice.CATEGORY.findStructureProperties(StructureFeature.BASTION_REMNANT) != null) {
            PracticeTypes.BastionType bastionType = PracticeTypes.BastionType.fromLabel(CategoryPreference.getValue("bastion_type"));
            if (bastionType != null) {
                int index;
                if (bastionType == PracticeTypes.BastionType.RANDOM) { index = this.possibleConfigs.indexOf(cir.getReturnValue()); }
                else { index = bastionType.id; }
                PeepoPractice.CATEGORY.putCustomValue("bastionType", index);
                cir.setReturnValue(this.possibleConfigs.get(index));
                return;
            }
        }
        PeepoPractice.CATEGORY.putCustomValue("bastionType", this.possibleConfigs.indexOf(cir.getReturnValue()));
    }
}
