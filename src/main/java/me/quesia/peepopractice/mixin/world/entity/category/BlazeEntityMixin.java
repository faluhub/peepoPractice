package me.quesia.peepopractice.mixin.world.entity.category;

import me.quesia.peepopractice.core.category.CategoryPreference;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlazeEntity.class)
public class BlazeEntityMixin extends HostileEntity {
    protected BlazeEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        if (CategoryPreference.getBoolValue("good_blaze_rates")) {
            this.dropStack(new ItemStack(Items.BLAZE_ROD));
            return;
        }
        super.dropLoot(source, causedByPlayer);
    }
}
