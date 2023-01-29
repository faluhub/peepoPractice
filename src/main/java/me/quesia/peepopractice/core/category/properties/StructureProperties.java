package me.quesia.peepopractice.core.category.properties;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

import java.util.function.Predicate;

public class StructureProperties {
    private ConfiguredStructureFeature<?, ?> structure;
    private ChunkPos chunkPos;
    private Direction orientation;
    private Predicate<ChunkPos> chunkPosPredicate;
    private Integer structureTopY;
    private boolean unique = false;
    public boolean hasGenerated = false;

    public ConfiguredStructureFeature<?, ?> getStructure() {
        return this.structure;
    }

    public StructureProperties setStructure(ConfiguredStructureFeature<?, ?> structure) {
        this.structure = structure;
        return this;
    }

    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    public StructureProperties setChunkPos(ChunkPos structureChunkPos) {
        this.chunkPos = structureChunkPos;
        return this;
    }

    public Direction getOrientation() {
        return this.orientation;
    }

    public StructureProperties setOrientation(Direction orientation) {
        this.orientation = orientation;
        return this;
    }

    public Integer getStructureTopY() {
        return this.structureTopY;
    }

    public StructureProperties setStructureTopY(int structureTopY) {
        this.structureTopY = structureTopY;
        return this;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public StructureProperties setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }
}
