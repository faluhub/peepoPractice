package me.falu.peepopractice.mixin.structure.category;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.preferences.PreferenceTypes;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BastionRemnantFeatureConfig.class)
public abstract class BastionRemnantFeatureConfigMixin {
    @Shadow @Final private List<StructurePoolFeatureConfig> possibleConfigs;

    @ModifyReturnValue(method = "getRandom", at = @At("RETURN"))
    private StructurePoolFeatureConfig peepoPractice$bastionType(StructurePoolFeatureConfig config) {
        if (PeepoPractice.CATEGORY.findStructureProperties(StructureFeature.BASTION_REMNANT) != null) {
            PreferenceTypes.BastionType bastionType = CategoryPreferences.BASTION_TYPE.getValue();
            int index = bastionType.equals(PreferenceTypes.BastionType.RANDOM) ? this.possibleConfigs.indexOf(config) : bastionType.id;
            PeepoPractice.CATEGORY.putCustomValue("bastionType", index);
            return this.possibleConfigs.get(index);
        }
        PeepoPractice.CATEGORY.putCustomValue("bastionType", this.possibleConfigs.indexOf(config));
        return config;
    }
}
