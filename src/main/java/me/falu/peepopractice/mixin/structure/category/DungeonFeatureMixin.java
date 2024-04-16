package me.falu.peepopractice.mixin.structure.category;

import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import net.minecraft.world.gen.feature.DungeonFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DungeonFeature.class)
public abstract class DungeonFeatureMixin {
    @Inject(method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$cancelDungeonGen(CallbackInfoReturnable<Boolean> cir) {
        if (CategoryPreferences.DISABLE_DUNGEONS.getBoolValue()) {
            cir.setReturnValue(true);
        }
    }
}
