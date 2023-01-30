package me.quesia.peepopractice.mixin.access;

import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("field_24748") long getField_24748();
}
