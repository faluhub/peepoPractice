package me.quesia.peepopractice.mixin.structure.category;

import me.quesia.peepopractice.core.category.CategoryPreference;
import net.minecraft.world.gen.feature.DungeonFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DungeonFeature.class)
public abstract class DungeonFeatureMixin {

    @Inject(method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$cancelDungeonGen(CallbackInfoReturnable<Boolean> cir) {
        if (CategoryPreference.getBoolValue("disable_dungeons")) {
            cir.setReturnValue(true);
        }
    }
}
