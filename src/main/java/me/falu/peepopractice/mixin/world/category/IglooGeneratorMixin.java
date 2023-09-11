package me.falu.peepopractice.mixin.world.category;

import me.falu.peepopractice.PeepoPractice;
import net.minecraft.structure.IglooGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(IglooGenerator.class)
public class IglooGeneratorMixin {
    @ModifyConstant(method = "addPieces", constant = @Constant(doubleValue = 0.5D))
    private static double peepoPractice$guaranteeIglooBasement(double constant) {
        if (PeepoPractice.CATEGORY.hasPermaValue("guaranteeIglooBasement")) {
            if ((Boolean) PeepoPractice.CATEGORY.getPermaValue("guaranteeIglooBasement")) {
                return 1.0D;
            }
        }
        return constant;
    }
}
