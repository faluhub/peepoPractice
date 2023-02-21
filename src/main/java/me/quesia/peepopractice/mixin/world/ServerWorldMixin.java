package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow public abstract ServerChunkManager getChunkManager();

    @Inject(method = "setSpawnPos", at = @At("TAIL"))
    private void removeTicket(BlockPos pos, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().isSpawnChunksDisabled()) {
            this.getChunkManager().removeTicket(ChunkTicketType.START, new ChunkPos(pos), 11, Unit.INSTANCE);
        }
    }

    @Inject(method = "getSpawnPos", at = @At("RETURN"), cancellable = true)
    private void getCustomSpawnPos(CallbackInfoReturnable<BlockPos> cir) {
        if (PeepoPractice.CATEGORY.hasPlayerProperties() && PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnPos()) {
            cir.setReturnValue(PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos());
        }
    }
}
