package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Redirect(method = "moveToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getSpawnPos()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos customSpawn(ServerWorld instance) {
        if (PeepoPractice.CATEGORY != null) {
            if (PeepoPractice.CATEGORY.getWorldRegistryKey() != null && PeepoPractice.CATEGORY.getWorldRegistryKey().equals(World.END)) {
                ServerWorld.createEndSpawnPlatform(instance);
            }
            if (PeepoPractice.CATEGORY.getSpawnPos() != null) {
                ((LevelProperties) instance.getLevelProperties()).setSpawnPos(PeepoPractice.CATEGORY.getSpawnPos());
                return PeepoPractice.CATEGORY.getSpawnPos();
            }
        }
        return instance.getSpawnPos();
    }

    @Redirect(method = "moveToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(Lnet/minecraft/util/math/BlockPos;FF)V", ordinal = 1))
    private void turnHead(ServerPlayerEntity instance, BlockPos blockPos, float yaw, float pitch) {
        if (PeepoPractice.CATEGORY != null && PeepoPractice.CATEGORY.getSpawnAngle() != null) {
            Vec2f angle = PeepoPractice.CATEGORY.getSpawnAngle();
            instance.refreshPositionAndAngles(blockPos, angle.x, angle.y);
            return;
        }
        instance.refreshPositionAndAngles(blockPos, yaw, pitch);
    }
}
