package me.quesia.peepopractice.mixin.world;

import com.mojang.authlib.GameProfile;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.event.ChangeDimensionSplitEvent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract void refreshPositionAfterTeleport(double x, double y, double z);

    @SuppressWarnings("unused")
    public ServerPlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
        super(world, blockPos, gameProfile);
    }

    @Inject(method = "moveToSpawn", at = @At("HEAD"), cancellable = true)
    private void customSpawn(ServerWorld world, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasPlayerProperties() && PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnPos()) {
            if (!(world.getLevelProperties() instanceof LevelProperties)) { return; }

            PeepoPractice.CATEGORY.getPlayerProperties().reset(new Random(world.getSeed()), world);

            if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey() && PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().equals(World.END)) {
                ServerWorld.createEndSpawnPlatform(world);
            }

            ((LevelProperties) world.getLevelProperties()).setSpawnPos(PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos());

            float yaw = 0.0F;
            float pitch = 0.0F;
            if (PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnAngle()) {
                Vec2f spawnAngle = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnAngle();
                yaw = spawnAngle.x;
                pitch = spawnAngle.y;
            }

            BlockPos spawnPos = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos();
            this.refreshPositionAndAngles(spawnPos, yaw, pitch);

            ci.cancel();
        }
    }

    @Inject(method = "changeDimension", at = @At("TAIL"))
    private void triggerSplitEvent(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof ChangeDimensionSplitEvent) {
                ChangeDimensionSplitEvent event = (ChangeDimensionSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasDimension() && event.getDimension() == destination.getRegistryKey()) {
                    event.endSplit(!this.isDead());
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            PeepoPractice.CATEGORY.getSplitEvent().endSplit(false);
        }
        super.onDeath(source);
    }
}
