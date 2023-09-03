package me.falu.peepopractice.mixin.world.entity.dragon;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {
    @Inject(method = "createDragon", at = @At("RETURN"))
    private void peepoPractice$tryKillDragon(CallbackInfoReturnable<EnderDragonEntity> cir) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            WorldProperties properties = PeepoPractice.CATEGORY.getWorldProperties();
            if (properties.getDragonKilled()) {
                cir.getReturnValue().damage(DamageSource.MAGIC, 5000);
            }
        }
    }
}
