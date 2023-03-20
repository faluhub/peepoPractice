package me.quesia.peepopractice.mixin.world.biome;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(VanillaLayeredBiomeSource.class)
public abstract class VanillaLayeredBiomeSourceMixin {

    @ModifyReturnValue(method = "getBiomeForNoiseGen", at = @At("RETURN"))
    private Biome peepoPractice$antiBiomeLogic(Biome biome, int biomeX, int biomeY, int biomeZ) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            {
                for (Map.Entry<Biome, Integer> entry : PeepoPractice.CATEGORY.getWorldProperties().getAntiBiomeRangeMap().entrySet()) {
                    if (entry.getValue() == null || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), entry.getValue())) {
                        return Biomes.PLAINS;
                    }
                }
            }
            {
                for (Map.Entry<Biome, Integer> entry : PeepoPractice.CATEGORY.getWorldProperties().getProBiomeRangeMap().entrySet()) {
                    if (entry.getValue() == null) {
                        break;
                    } else if (new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), entry.getValue())) {
                        return entry.getKey();
                    }
                }
            }
        }
        return biome;
    }
}
