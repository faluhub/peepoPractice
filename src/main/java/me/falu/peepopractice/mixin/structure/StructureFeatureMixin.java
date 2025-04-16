package me.falu.peepopractice.mixin.structure;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StructureFeature.class)
public abstract class StructureFeatureMixin<C extends FeatureConfig> {
    @Shadow
    protected abstract boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, C featureConfig);
    @Shadow
    public abstract ChunkPos getStartChunk(StructureConfig structureConfig, long l, ChunkRandom chunkRandom, int i, int j);
    @Shadow
    protected abstract StructureStart<C> createStart(int i, int j, BlockBox blockBox, int k, long l);
    @Shadow
    public abstract String getName();

    @Unique
    @SuppressWarnings("UnreachableCode")
    private boolean checkArtificialStructure(ChunkPos chunkPos) {
        for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
            if (properties.isSameStructure((StructureFeature<?>) (Object) this)) {
                if (properties.hasChunkPos() && properties.getChunkPos().equals(chunkPos)) {
                    if (!properties.isGeneratable() && !properties.hasGenerated()) {
                        properties.setGenerated();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @author falu
     * @reason Custom structure starts
     * NOTE: Don't hate, it's copied from the source.
     */
    @Overwrite
    @SuppressWarnings("UnreachableCode")
    public StructureStart<?> tryPlaceStart(ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome, int i, ChunkRandom chunkRandom, StructureConfig structureConfig, C featureConfig) {
        ChunkPos chunkPos2 = this.getStartChunk(structureConfig, l, chunkRandom, chunkPos.x, chunkPos.z);
        StructureProperties props = PeepoPractice.CATEGORY.findStructureProperties((StructureFeature<?>) (Object) this);
        boolean bl1 = this.checkArtificialStructure(chunkPos);
        boolean bl2 = !bl1 && (props == null || props.isGeneratable()) && chunkPos.x == chunkPos2.x && chunkPos.z == chunkPos2.z && this.shouldStartAt(chunkGenerator, biomeSource, l, chunkRandom, chunkPos.x, chunkPos.z, biome, chunkPos2, featureConfig);
        if (bl1 || bl2) {
            StructureStart<C> structureStart = this.createStart(chunkPos.x, chunkPos.z, BlockBox.empty(), i, l);
            structureStart.init(chunkGenerator, structureManager, chunkPos.x, chunkPos.z, biome, featureConfig);
            if (structureStart.hasChildren()) {
                for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
                    if (properties.hasStructureTopY()) {
                        if (properties.isSameStructure((StructureFeature<?>) (Object) this)) {
                            int difference = properties.getStructureTopY() - structureStart.getChildren().get(0).getBoundingBox().maxY;
                            for (StructurePiece piece : structureStart.getChildren()) {
                                piece.translate(0, difference, 0);
                            }
                            break;
                        }
                    }
                }
                return structureStart;
            }
        }
        return StructureStart.DEFAULT;
    }
}
