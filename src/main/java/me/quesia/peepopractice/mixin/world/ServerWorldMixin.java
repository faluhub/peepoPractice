package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Redirect(method = "setSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;addTicket(Lnet/minecraft/server/world/ChunkTicketType;Lnet/minecraft/util/math/ChunkPos;ILjava/lang/Object;)V"))
    private <T> void noTicket(ServerChunkManager instance, ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().isSpawnChunksEnabled()) {
            instance.addTicket(ticketType, pos, radius, argument);
        }
    }
}
