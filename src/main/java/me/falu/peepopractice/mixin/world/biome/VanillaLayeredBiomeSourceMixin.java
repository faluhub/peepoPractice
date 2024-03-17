package me.falu.peepopractice.mixin.world.biome;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VanillaLayeredBiomeSource.class)
public abstract class VanillaLayeredBiomeSourceMixin {
    @ModifyReturnValue(method = "getBiomeForNoiseGen", at = @At("RETURN"))
    private Biome peepoPractice$antiBiomeLogic(Biome biome, int biomeX, int biomeY, int biomeZ) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            for (WorldProperties.BiomeModification entry : PeepoPractice.CATEGORY.getWorldProperties().getProBiomes()) {
                if (entry.getRange().isValidDimension(World.OVERWORLD)) {
                    if (new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), entry.getRange().getRange())) {
                        if (entry.getRange().shouldPlace()) {
                            return entry.getBiome();
                        }
                    }
                }
            }
            for (WorldProperties.BiomeModification entry : PeepoPractice.CATEGORY.getWorldProperties().getAntiBiomes()) {
                if (entry.getRange().isValidDimension(World.OVERWORLD)) {
                    if (entry.isInfinite() || new BlockPos(biomeX, biomeY, biomeZ).isWithinDistance(new Vec3i(0, 62, 0), entry.getRange().getRange())) {
                        if (biome.equals(entry.getBiome())) {
                            return entry.hasReplacement() ? entry.getReplacement() : Biomes.PLAINS;
                        }
                    }
                }
            }
        }
        return biome;
    }
}
