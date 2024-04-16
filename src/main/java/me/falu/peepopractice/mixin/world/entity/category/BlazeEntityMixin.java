package me.falu.peepopractice.mixin.world.entity.category;

import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlazeEntity.class)
public abstract class BlazeEntityMixin extends MobEntityMixin {
    protected BlazeEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void peepoPractice$onDropLoot(CallbackInfo ci) {
        if (CategoryPreferences.GOOD_BLAZE_RATES.getBoolValue()) {
            this.dropStack(new ItemStack(Items.BLAZE_ROD));
            ci.cancel();
        }
    }
}
