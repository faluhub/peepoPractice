package me.quesia.peepopractice.mixin.world.entity;

import me.quesia.peepopractice.core.CustomLootTables;
import me.quesia.peepopractice.core.category.CategoryPreference;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Inject(method = "method_29276", at = @At("RETURN"), cancellable = true)
    private static void danceOnPlayerKill(LivingEntity livingEntity, LivingEntity livingEntity2, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity2.getType().equals(EntityType.PLAYER)) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "getBarteredItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootManager;getTable(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;"))
    private static LootTable customLootTable(LootManager instance, Identifier id) {
        if (CategoryPreference.getBoolValue("ranked_loot_table")) { return CustomLootTables.CUSTOM_PIGLIN_BARTER; }
        return instance.getTable(id);
    }
}
