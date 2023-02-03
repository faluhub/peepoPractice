package me.quesia.peepopractice.mixin.world.biome;

import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Inject(method = "getBiomeForNoiseGen", at = @At("RETURN"), cancellable = true)
    private void removeBasaltRegion(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            Map<Biome, Integer> antiBiomeRangeMap = PeepoPractice.CATEGORY.getWorldProperties().getAntiBiomeRangeMap();
            AtomicBoolean shouldStop = new AtomicBoolean();
            antiBiomeRangeMap.forEach((k, v) -> {
                if (!shouldStop.get() && k.getName().equals(cir.getReturnValue().getName())) {
                    if (v == null || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), v)) {
                        cir.setReturnValue(Biomes.NETHER_WASTES);
                        shouldStop.set(true);
                    }
                }
            });
        }
    }
}
