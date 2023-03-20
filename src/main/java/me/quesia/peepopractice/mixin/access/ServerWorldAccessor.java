package me.quesia.peepopractice.mixin.access;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {
    @Accessor("inEntityTick") boolean peepoPractice$getInEntityTick();
}
