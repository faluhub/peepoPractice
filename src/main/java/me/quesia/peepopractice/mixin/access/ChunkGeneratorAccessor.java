package me.quesia.peepopractice.mixin.access;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("field_24748") long getField_24748();
    @Accessor("field_24749") List<ChunkPos> getField_24749();
    @Invoker("method_28509") void invokeMethod_28509();
}
