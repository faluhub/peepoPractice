package me.quesia.peepopractice.mixin.world.biome;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
public abstract class MultiNoiseBiomeSourceMixin {

    @ModifyReturnValue(method = "getBiomeForNoiseGen", at = @At("RETURN"))
    private Biome peepoPractice$antiBiomeLogic(Biome biome, int biomeX, int biomeY, int biomeZ) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            {
                for (Map.Entry<Biome, Integer> entry : PeepoPractice.CATEGORY.getWorldProperties().getAntiBiomeRangeMap().entrySet()) {
                    if (entry.getValue() == null || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), entry.getValue())) {
                        return Biomes.NETHER_WASTES;
                    }
                }
            }
            {
                for (Map.Entry<Biome, Integer> entry : PeepoPractice.CATEGORY.getWorldProperties().getProBiomeRangeMap().entrySet()) {
                    if (entry.getValue() == null || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), entry.getValue())) {
                        return entry.getKey();
                    }
                }
            }
        }
        return biome;
    }

    @Inject(method = "getBiomeForNoiseGen", at = @At("RETURN"), cancellable = true)
    private void peepoPractice$antiBiomeLogic(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            {
                Map<Biome, Integer> antiBiomeRangeMap = PeepoPractice.CATEGORY.getWorldProperties().getAntiBiomeRangeMap();
                AtomicBoolean shouldStop = new AtomicBoolean(false);
                antiBiomeRangeMap.forEach((k, v) -> {
                    if (!shouldStop.get() && k.getName().equals(cir.getReturnValue().getName())) {
                        if (v == null || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), v)) {
                            cir.setReturnValue(Biomes.NETHER_WASTES);
                            shouldStop.set(true);
                        }
                    }
                });
            }
            {
                Map<Biome, Integer> proBiomeRangeMap = PeepoPractice.CATEGORY.getWorldProperties().getProBiomeRangeMap();
                AtomicBoolean shouldStop = new AtomicBoolean(false);
                proBiomeRangeMap.forEach((k, v) -> {
                    if (!shouldStop.get()) {
                        if (v == null || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), v)) {
                            cir.setReturnValue(k);
                            shouldStop.set(true);
                        }
                    }
                });
            }
        }
    }
}
