package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.CategoryPreference;
import net.minecraft.block.Blocks;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow public abstract ServerChunkManager getChunkManager();
    @Shadow @Final public static BlockPos END_SPAWN_POS;

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

    @Inject(method = "createEndSpawnPlatform", at = @At("TAIL"))
    private static void noCage(ServerWorld world, CallbackInfo ci) {
        if (CategoryPreference.getBoolValue("no_cage_spawn")) {
            BlockPos blockPos = END_SPAWN_POS;
            int i = blockPos.getX();
            int j = blockPos.getY() - 2;
            int k = blockPos.getZ();
            int xOffset = 6;
            int yOffset = 15;
            BlockPos.iterate(i - xOffset, j + 1, k - xOffset, i + xOffset, j + yOffset, k + xOffset).forEach(x -> world.setBlockState(x, Blocks.AIR.getDefaultState()));
        }
    }
}
