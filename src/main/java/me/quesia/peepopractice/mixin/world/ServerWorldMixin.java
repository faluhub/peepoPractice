package me.quesia.peepopractice.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.CategoryPreference;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
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

    @Shadow @Final public static BlockPos END_SPAWN_POS;

    @WrapWithCondition(method = "setSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;addTicket(Lnet/minecraft/server/world/ChunkTicketType;Lnet/minecraft/util/math/ChunkPos;ILjava/lang/Object;)V"))
    private boolean peepoPractice$removeTicket(ServerChunkManager chunkManager, ChunkTicketType<?> ticketType, ChunkPos pos, int radius, Object argument) {
        return !(PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().isSpawnChunksDisabled());
    }

    @ModifyReturnValue(method = "getSpawnPos", at = @At("RETURN"))
    private BlockPos peepoPractice$getCustomSpawnPos(BlockPos pos) {
        if (PeepoPractice.CATEGORY.hasPlayerProperties() && PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnPos()) {
            return PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos();
        }
        return pos;
    }

    @Inject(method = "createEndSpawnPlatform", at = @At("TAIL"))
    private static void peepoPractice$noCage(ServerWorld world, CallbackInfo ci) {
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
