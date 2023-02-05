package me.quesia.peepopractice.mixin.world;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow public abstract ServerChunkManager getChunkManager();

    @Inject(method = "setSpawnPos", at = @At("TAIL"))
    private void removeTicket(BlockPos pos, CallbackInfo ci) {
        this.getChunkManager().removeTicket(ChunkTicketType.START, new ChunkPos(pos), 11, Unit.INSTANCE);
    }
}
