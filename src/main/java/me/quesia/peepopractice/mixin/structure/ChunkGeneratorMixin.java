package me.quesia.peepopractice.mixin.structure;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.client.MinecraftClient;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Shadow @Final protected BiomeSource biomeSource;
    @Shadow protected abstract void method_28508(ConfiguredStructureFeature<?, ?> configuredStructureFeature, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome);

    private boolean isUniqueStronghold() {
        for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
            if (properties.getStructure().field_24835.getName().equals(StructureFeature.STRONGHOLD.getName()) && properties.isUnique()) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "setStructureStarts", at = @At("HEAD"))
    private void customStructureStarts(StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long l, CallbackInfo ci) {
        for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null) {
                ChunkPos chunkPos = client.world.getChunk(properties.getChunkPos().x, properties.getChunkPos().z, ChunkStatus.BIOMES).getPos();
                Biome biome = this.biomeSource.getBiomeForNoiseGen((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
                this.method_28508(properties.getStructure(), structureAccessor, chunk, structureManager, l, chunkPos, biome);
            }
        }
    }

    @Redirect(method = "setStructureStarts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;method_28508(Lnet/minecraft/world/gen/feature/ConfiguredStructureFeature;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/structure/StructureManager;JLnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/Biome;)V", ordinal = 0))
    private void cancelStrongholds(ChunkGenerator instance, ConfiguredStructureFeature<?, ?> configuredStructureFeature, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome) {
        if (this.isUniqueStronghold()) { return; }
        this.method_28508(configuredStructureFeature, structureAccessor, chunk, structureManager, l, chunkPos, biome);
    }

    @Inject(method = "method_28509", at = @At("HEAD"), cancellable = true)
    private void cancelStrongholdLocations(CallbackInfo ci) {
        if (this.isUniqueStronghold()) { ci.cancel(); }
    }
}
