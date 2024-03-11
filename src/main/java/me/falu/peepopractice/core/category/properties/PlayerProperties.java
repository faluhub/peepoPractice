package me.falu.peepopractice.core.category.properties;

import com.google.common.collect.Lists;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.exception.NotInitializedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("UnusedDeclaration")
public class PlayerProperties extends BaseProperties {
    private final List<String> commands = new ArrayList<>();
    private final List<PotionEffect> potionEffects = new ArrayList<>();
    private BlockPos spawnPos;
    private PracticeCategory.ExecuteReturnTask<BlockPos> spawnPosTask;
    private Vec2f spawnAngle;
    private PracticeCategory.ExecuteReturnTask<Vec2f> spawnAngleTask;
    private EntityType<? extends Entity> vehicle;

    public BlockPos getSpawnPos() {
        return this.spawnPos != null ? this.spawnPos : new BlockPos(0, 62, 0);
    }

    public PlayerProperties setSpawnPos(BlockPos spawnPos) {
        this.spawnPos = spawnPos;
        return this;
    }

    public PlayerProperties setSpawnPos(PracticeCategory.ExecuteReturnTask<BlockPos> spawnPosTask) {
        this.spawnPosTask = spawnPosTask;
        return this;
    }

    public boolean hasSpawnPos() {
        return this.spawnPos != null;
    }

    public Vec2f getSpawnAngle() {
        return this.spawnAngle != null ? this.spawnAngle : new Vec2f(0.0F, 0.0F);
    }

    public PlayerProperties setSpawnAngle(PracticeCategory.ExecuteReturnTask<Vec2f> spawnAngleTask) {
        this.spawnAngleTask = spawnAngleTask;
        return this;
    }

    public boolean hasSpawnAngle() {
        return this.spawnAngle != null;
    }

    public PlayerProperties setSpawnAngle(float yaw, float pitch) {
        this.spawnAngle = new Vec2f(yaw, pitch);
        return this;
    }

    public EntityType<? extends Entity> getVehicle() {
        return this.vehicle;
    }

    public PlayerProperties setVehicle(EntityType<? extends Entity> vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public boolean hasVehicle() {
        return this.vehicle != null;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public PlayerProperties runCommands(String[] commands) {
        this.commands.addAll(Lists.newArrayList(commands));
        return this;
    }

    public PlayerProperties runCommand(String command) {
        this.commands.add(command);
        return this;
    }

    public List<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    public PlayerProperties addPotionEffect(PotionEffect potionEffect) {
        this.potionEffects.add(potionEffect);
        return this;
    }

    public void reset(Random random, ServerWorld world) throws NotInitializedException {
        if (this.spawnPosTask != null) {
            this.setSpawnPos(this.spawnPosTask.execute(this.getCategory(), random, world));
        }
        if (this.spawnAngleTask != null) {
            Vec2f vec = this.spawnAngleTask.execute(this.getCategory(), random, world);
            if (vec != null) {
                this.setSpawnAngle(vec.x, vec.y);
            }
        }
    }

    public static class PotionEffect {
        private StatusEffect effect;
        private int amplifier = 0;
        private int duration = Integer.MAX_VALUE;
        private PracticeCategory.ExecuteReturnTask<Boolean> condition;

        public StatusEffect getEffect() {
            return this.effect;
        }

        public PotionEffect setEffect(StatusEffect effect) {
            this.effect = effect;
            return this;
        }

        public boolean hasEffect() {
            return this.effect != null;
        }

        public int getAmplifier() {
            return this.amplifier;
        }

        public PotionEffect setAmplifier(int amplifier) {
            this.amplifier = amplifier;
            return this;
        }

        public int getDuration() {
            return this.duration;
        }

        public PotionEffect setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public PracticeCategory.ExecuteReturnTask<Boolean> getCondition() {
            return this.condition;
        }

        public PotionEffect setCondition(PracticeCategory.ExecuteReturnTask<Boolean> condition) {
            this.condition = condition;
            return this;
        }

        public boolean hasCondition() {
            return this.condition != null;
        }
    }
}
