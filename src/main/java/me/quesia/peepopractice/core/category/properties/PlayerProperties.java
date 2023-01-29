package me.quesia.peepopractice.core.category.properties;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

public class PlayerProperties {
    private BlockPos spawnPos;
    private Vec2f spawnAngle;

    public BlockPos getSpawnPos() {
        return this.spawnPos;
    }

    public PlayerProperties setSpawnPos(BlockPos spawnPos) {
        this.spawnPos = spawnPos;
        return this;
    }

    public Vec2f getSpawnAngle() {
        return this.spawnAngle;
    }

    public PlayerProperties setSpawnAngle(float yaw, float pitch) {
        this.spawnAngle = new Vec2f(yaw, pitch);
        return this;
    }
}
