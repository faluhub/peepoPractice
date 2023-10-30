package me.falu.peepopractice.mixin.world.entity;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.properties.event.EnterVehicleSplitEvent;
import me.falu.peepopractice.core.exception.NotInitializedException;
import me.falu.peepopractice.core.category.CategoryPreference;
import me.falu.peepopractice.core.category.PracticeTypes;
import me.falu.peepopractice.core.category.properties.event.ChangeDimensionSplitEvent;
import me.falu.peepopractice.core.category.properties.event.SplitEvent;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends LivingEntity {
    @Shadow public abstract void refreshPositionAfterTeleport(double x, double y, double z);
    @Shadow @Nullable public abstract BlockPos getSpawnPointPosition();
    @Shadow public abstract void setGameMode(GameMode gameMode);
    @Shadow public abstract void sendMessage(Text message, boolean actionBar);
    @Shadow public abstract boolean startRiding(Entity entity, boolean force);
    @Shadow private int joinInvulnerabilityTicks;
    @Shadow public abstract ServerWorld getServerWorld();
    @Unique private Long comparingTime;
    @Unique private PracticeTypes.PaceTimerShowType showType;

    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "moveToSpawn", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$customSpawn(ServerWorld world, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            PracticeTypes.CompareType compareType = PracticeTypes.CompareType.fromLabel(CategoryPreference.getOrDefault(PeepoPractice.CATEGORY, "compare_type", PracticeTypes.CompareType.PB.getLabel()));
            if (compareType != null) {
                switch (compareType) {
                    case PB:
                        this.comparingTime = PeepoPractice.CATEGORY.getSplitEvent().hasPb() ? PeepoPractice.CATEGORY.getSplitEvent().getPbLong() : null;
                        break;
                    case AVERAGE:
                        this.comparingTime = PeepoPractice.CATEGORY.getSplitEvent().hasCompletedTimes() ? PeepoPractice.CATEGORY.getSplitEvent().findAverage() : null;
                        break;
                }
            }
            this.showType = PracticeTypes.PaceTimerShowType.fromLabel(CategoryPreference.getOrDefault(PeepoPractice.CATEGORY, "pace_timer_show_type", PracticeTypes.PaceTimerShowType.ALWAYS.getLabel()));
        }

        if (PeepoPractice.CATEGORY.hasPlayerProperties()) {
            if (!(world.getLevelProperties() instanceof LevelProperties)) { return; }

            if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey() && PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().equals(World.END)) {
                ServerWorld.createEndSpawnPlatform(world);
            }

            if (PeepoPractice.RETRY_PLAYER_INITIALIZATION) {
                try {
                    PeepoPractice.CATEGORY.getPlayerProperties().reset(new Random(world.getSeed()), world);
                    PeepoPractice.log("Infinite loop resolved for " + PeepoPractice.CATEGORY.getId() + ".");
                } catch (NotInitializedException ignored) {
                    PeepoPractice.log("Failed to resolve infinite loop for " + PeepoPractice.CATEGORY.getId() + ".");
                }
                PeepoPractice.RETRY_PLAYER_INITIALIZATION = false;
            }

            float yaw = 0.0F;
            float pitch = 0.0F;
            if (PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnAngle()) {
                Vec2f spawnAngle = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnAngle();
                yaw = spawnAngle.x;
                pitch = spawnAngle.y;
            }

            BlockPos spawnPos = world.getSpawnPos();
            if (PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnPos()) {
                spawnPos = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos();
                ((LevelProperties) world.getLevelProperties()).setSpawnPos(spawnPos);
            }

            if (PeepoPractice.CATEGORY.getPlayerProperties().hasVehicle() && !world.isClient) {
                Entity entity = PeepoPractice.CATEGORY.getPlayerProperties().getVehicle().create(world, null, null, null, spawnPos, SpawnReason.STRUCTURE, true, false);
                if (entity != null) {
                    world.spawnEntity(entity);
                    entity.updatePosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                    entity.prevX = spawnPos.getX();
                    entity.prevY = spawnPos.getY();
                    entity.prevZ = spawnPos.getZ();
                    entity.yaw = entity.prevYaw = yaw;
                    entity.pitch = entity.prevPitch = pitch;
                    entity.setVelocity(Vec3d.ZERO);
                    this.refreshPositionAndAngles(spawnPos, yaw, pitch);
                    this.startRiding(entity, true);
                } else {
                    PeepoPractice.LOGGER.warn("Couldn't create vehicle entity.");
                }
            } else {
                this.refreshPositionAndAngles(spawnPos, yaw, pitch);
            }

            ci.cancel();
        }
    }

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$checkVehicleSplit(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof EnterVehicleSplitEvent) {
                EnterVehicleSplitEvent event = (EnterVehicleSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (event.hasVehicle() && event.getVehicle().equals(entity.getType())) {
                    if (InGameTimer.getInstance().isCompleted() && this.getScoreboardTags().contains("completed")) {
                        cir.setReturnValue(false);
                        return;
                    }
                    event.complete(!this.isDead());
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void peepoPractice$removeSpawnProtection(CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            this.joinInvulnerabilityTicks = 0;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void peepoPractice$setPaceMessage(CallbackInfo ci) {
        if (this.showType == null || this.showType.equals(PracticeTypes.PaceTimerShowType.NEVER) || (this.showType.equals(PracticeTypes.PaceTimerShowType.END) && !InGameTimer.getInstance().isCompleted())) { return; }
        long igt = InGameTimer.getInstance().getInGameTime();
        if (this.comparingTime != null) {
            long difference = this.comparingTime - igt;
            boolean flip = false;
            if (difference <= 0) {
                difference = igt - this.comparingTime;
                flip = true;
            }
            String timeString = Formatting.GRAY + "Pace: ";
            timeString += !flip ? Formatting.GREEN + "-" : Formatting.RED + "+";
            timeString += SplitEvent.getTimeString(difference);
            this.sendMessage(new LiteralText(timeString), true);
        } else {
            this.sendMessage(new LiteralText(Formatting.GRAY + I18n.translate("peepopractice.text.pace") + ": " + Formatting.GREEN + "-" + SplitEvent.getTimeString(igt)), true);
        }
    }

    @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$triggerSplitEvent(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        RegistryKey<World> current = this.getServerWorld().getRegistryKey();
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof ChangeDimensionSplitEvent) {
                if (InGameTimer.getInstance().isCompleted() && this.getScoreboardTags().contains("completed")) {
                    cir.setReturnValue(this);
                    return;
                }

                ChangeDimensionSplitEvent event = (ChangeDimensionSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                if (InGameTimer.getInstance().getStatus() != TimerStatus.NONE && event.hasToDimension() && event.getToDimension() == destination.getRegistryKey()) {
                    if (!event.hasFromDimension() || event.getFromDimension().equals(current)) {
                        event.complete(!this.isDead());
                        cir.setReturnValue(this);
                    }
                }
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    protected void peepoPractice$onPlayerDeath(DamageSource source, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            boolean end = true;
            if (this.getServer() != null && this.getServer().getOverworld() != null && this.getSpawnPointPosition() != null) {
                if (this.getSpawnPointPosition() != this.getServer().getOverworld().getSpawnPos()) {
                    if (this.getSpawnPointPosition().isWithinDistance(new BlockPos(this.getPos().x, this.getSpawnPointPosition().getY(), this.getPos().z), 5.0D)) {
                        end = false;
                    }
                }
            }
            if (end) {
                this.setGameMode(GameMode.SPECTATOR);
                PeepoPractice.CATEGORY.getSplitEvent().complete(false);
            }
        }
    }
}
