package me.falu.peepopractice.mixin.world;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin {
    @Shadow @Nullable protected Identifier lootTableId;

    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(method = "createMenu", at = @At("HEAD"), cancellable = true)
    protected void peepoPractice$onCreateMenu(CallbackInfoReturnable<ScreenHandler> cir) {
    }

    @Inject(method = "checkLootInteraction", at = @At("HEAD"))
    protected void peepoPractice$onCheckLootInteraction(CallbackInfo ci) {
    }
}
