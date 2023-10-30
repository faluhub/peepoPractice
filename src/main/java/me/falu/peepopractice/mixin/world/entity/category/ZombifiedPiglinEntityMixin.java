package me.falu.peepopractice.mixin.world.entity.category;

import me.falu.peepopractice.core.category.CategoryPreference;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ZombifiedPiglinEntity.class)
public class ZombifiedPiglinEntityMixin {
    @Unique
    private static final LocationPredicate BASTION_PREDICATE = LocationPredicate.feature(StructureFeature.BASTION_REMNANT);

    @Inject(method = "canSpawn(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)Z", at = @At("RETURN"), cancellable = true)
    private static void noBastionPigmen(EntityType<ZombifiedPiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (!CategoryPreference.getBoolValue("zombie_pigmen") && world instanceof ServerWorld) {
            if (BASTION_PREDICATE.test((ServerWorld) world, pos.getX(), pos.getY(), pos.getZ())) {
                cir.setReturnValue(false);
            }
        }
    }
}
