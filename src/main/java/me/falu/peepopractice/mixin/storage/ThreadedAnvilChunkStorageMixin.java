package me.falu.peepopractice.mixin.storage;

import com.mojang.datafixers.DataFixer;
import me.falu.peepopractice.owner.GenerationShutdownOwner;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage implements GenerationShutdownOwner {
    @Shadow @Final private ChunkTaskPrioritySystem chunkTaskPrioritySystem;
    @Shadow protected abstract PointOfInterestStorage getPointOfInterestStorage();

    public ThreadedAnvilChunkStorageMixin(File file, DataFixer dataFixer, boolean bl) {
        super(file, dataFixer, bl);
    }

    @Override
    public void peepopractice$shutdown() {
        try {
            this.chunkTaskPrioritySystem.close();
            ((GenerationShutdownOwner) this.getPointOfInterestStorage().worker).peepopractice$shutdown();
        } finally {
            ((GenerationShutdownOwner) this.worker).peepopractice$shutdown();
        }
    }
}
