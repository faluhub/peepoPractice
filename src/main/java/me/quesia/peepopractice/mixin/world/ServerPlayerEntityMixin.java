package me.quesia.peepopractice.mixin.world;

import com.mojang.authlib.GameProfile;
import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
        super(world, blockPos, gameProfile);
    }

    @Shadow public abstract void refreshPositionAfterTeleport(double x, double y, double z);

    @Inject(method = "moveToSpawn", at = @At("HEAD"), cancellable = true)
    private void customSpawn(ServerWorld world, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY != null) {
            if (PeepoPractice.CATEGORY.getWorldProperties() != null && PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey() != null && PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().equals(World.END)) {
                ServerWorld.createEndSpawnPlatform(world);
            }
            if (PeepoPractice.CATEGORY.getPlayerProperties() != null && PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos() != null) {
                ((LevelProperties) world.getLevelProperties()).setSpawnPos(PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos());
                float yaw = 0.0F;
                float pitch = 0.0F;
                if (PeepoPractice.CATEGORY.getPlayerProperties().getSpawnAngle() != null) {
                    yaw = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnAngle().x;
                    pitch = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnAngle().y;
                }
                BlockPos spawnPos = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos();
                this.refreshPositionAndAngles(spawnPos, yaw, pitch);
            }

            ci.cancel();
        }
    }
}
