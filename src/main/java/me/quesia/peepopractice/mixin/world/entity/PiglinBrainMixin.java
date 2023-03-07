package me.quesia.peepopractice.mixin.world.entity;

import com.google.common.collect.Lists;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.CategoryPreference;
import me.quesia.peepopractice.core.loot.PiglinBarterState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    private static PiglinEntity PIGLIN;

    @Inject(method = "method_29276", at = @At("RETURN"), cancellable = true)
    private static void danceOnPlayerKill(LivingEntity livingEntity, LivingEntity livingEntity2, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity2.getType().equals(EntityType.PLAYER)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getBarteredItem", at = @At("HEAD"))
    private static void capturePiglin(PiglinEntity piglin, CallbackInfoReturnable<List<ItemStack>> cir) {
        PIGLIN = piglin;
    }

    @Inject(method = "getBarteredItem", at = @At("RETURN"), cancellable = true)
    private static void guaranteeTrades(PiglinEntity piglin, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (CategoryPreference.getBoolValue("ranked_loot_table")) {
            MinecraftServer server = PIGLIN.getServer();
            List<ItemStack> itemStacks = cir.getReturnValue();
            if (server == null || itemStacks.size() == 0) {
                return;
            }
            ServerWorld overworld = server.getOverworld();
            PiglinBarterState piglinBarterState = overworld.getPersistentStateManager().getOrCreate(PiglinBarterState::new, "piglin_barters");
            cir.setReturnValue(Lists.newArrayList(piglinBarterState.guaranteeItem(piglin, itemStacks.get(0), new Random())));
        }
    }
}
