package me.quesia.peepopractice.mixin.structure;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
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

@Mixin(StructureFeature.class)
public abstract class StructureFeatureMixin<C extends FeatureConfig> {
    @Shadow protected abstract boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, C featureConfig);
    @Shadow public abstract ChunkPos method_27218(StructureConfig structureConfig, long l, ChunkRandom chunkRandom, int i, int j);
    @Shadow protected abstract StructureStart<C> method_28656(int i, int j, BlockBox blockBox, int k, long l);
    @Shadow public abstract String getName();

    private boolean checkArtificialStructure(ChunkPos chunkPos) {
        if (PeepoPractice.CATEGORY != null && !PeepoPractice.CATEGORY.getStructureProperties().isEmpty()) {
            for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
                if (properties.getStructure().field_24835.getName().equals(this.getName())) {
                    if (properties.getChunkPos().equals(chunkPos)) {
                        if (!properties.isUnique() || (properties.isUnique() && !properties.hasGenerated)) {
                            properties.hasGenerated = true;
                            PeepoPractice.CURRENT_ORIENTATION = properties.getOrientation();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @author Quesia
     * @reason Custom structure starts
     */
    @Overwrite
    public StructureStart<?> method_28657(ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome, int i, ChunkRandom chunkRandom, StructureConfig structureConfig, C featureConfig) {
        ChunkPos chunkPos2 = this.method_27218(structureConfig, l, chunkRandom, chunkPos.x, chunkPos.z);
        if (this.checkArtificialStructure(chunkPos) || (chunkPos.x == chunkPos2.x && chunkPos.z == chunkPos2.z && this.shouldStartAt(chunkGenerator, biomeSource, l, chunkRandom, chunkPos.x, chunkPos.z, biome, chunkPos2, featureConfig))) {
            final StructureStart<C> structureStart = this.method_28656(chunkPos.x, chunkPos.z, BlockBox.empty(), i, l);
            structureStart.init(chunkGenerator, structureManager, chunkPos.x, chunkPos.z, biome, featureConfig);
            if (structureStart.hasChildren()) {
                if (PeepoPractice.CATEGORY != null && !PeepoPractice.CATEGORY.getStructureProperties().isEmpty()) {
                    for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
                        if (properties.getStructureTopY() != null) {
                            if (properties.getStructure().field_24835.getName().equals(this.getName())) {
                                int topY = properties.getStructureTopY();
                                int curMaxY = structureStart.getChildren().get(0).getBoundingBox().maxY;
                                int difference = topY - curMaxY;
                                for (StructurePiece piece : structureStart.getChildren()) {
                                    piece.translate(0, difference, 0);
                                }
                                Direction orientation = properties.getOrientation();
                                structureStart.getChildren().get(0).setOrientation(orientation);
                                break;
                            }
                        }
                    }
                }
                return structureStart;
            }
        }
        return StructureStart.DEFAULT;
    }
}
