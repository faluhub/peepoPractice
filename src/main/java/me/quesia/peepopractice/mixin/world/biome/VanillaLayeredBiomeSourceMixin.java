package me.quesia.peepopractice.mixin.world.biome;

import com.mojang.serialization.Codec;
import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(VanillaLayeredBiomeSource.class)
public abstract class VanillaLayeredBiomeSourceMixin {
    @Shadow protected abstract Codec<? extends BiomeSource> method_28442();

    @Shadow @Final private BiomeLayerSampler biomeSampler;

    @Inject(method = "getBiomeForNoiseGen", at = @At("RETURN"), cancellable = true)
    private void antiBiomeLogic(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            {
                Map<Biome, Integer> antiBiomeRangeMap = PeepoPractice.CATEGORY.getWorldProperties().getAntiBiomeRangeMap();
                AtomicBoolean shouldStop = new AtomicBoolean(false);
                antiBiomeRangeMap.forEach((k, v) -> {
                    if (!shouldStop.get() && k.getName().equals(cir.getReturnValue().getName())) {
                        if (v == null || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), v)) {
                            cir.setReturnValue(Biomes.PLAINS);
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
                        if (v == null) { shouldStop.set(true); }
                        else if (new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), v)) {
                            cir.setReturnValue(k);
                            shouldStop.set(true);
                        }
                    }
                });
            }
        }
    }
}
