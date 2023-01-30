package me.quesia.peepopractice.core.category.properties;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

@SuppressWarnings("UnusedDeclaration")
public class PlayerProperties extends BaseProperties {
    private BlockPos spawnPos;
    private Vec2f spawnAngle;

    public BlockPos getSpawnPos() {
        return this.spawnPos;
    }

    public boolean hasSpawnPos() {
        return this.spawnPos != null;
    }

    public PlayerProperties setSpawnPos(BlockPos spawnPos) {
        this.spawnPos = spawnPos;
        return this;
    }

    public Vec2f getSpawnAngle() {
        return this.spawnAngle;
    }

    public boolean hasSpawnAngle() {
        return this.spawnAngle != null;
    }

    public PlayerProperties setSpawnAngle(float yaw, float pitch) {
        this.spawnAngle = new Vec2f(yaw, pitch);
        return this;
    }
}
