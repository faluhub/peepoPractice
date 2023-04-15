package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.utils.InventoryUtils;
import me.quesia.peepopractice.core.category.PracticeCategories;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @ModifyVariable(method = "onPlayerConnect", at = @At("STORE"))
    private RegistryKey<World> peepoPractice$otherDimension(RegistryKey<World> value) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) {
            return PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey();
        }
        return value;
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void peepoPractice$setInventory(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            MinecraftClient.getInstance().inGameHud.setTitles(null, null, -1, -1, -1);
            if (InventoryUtils.PREVIOUS_INVENTORY != null) {
                PeepoPractice.log("Using inventory from previous split.");
                player.inventory.clone(InventoryUtils.PREVIOUS_INVENTORY);
            } else {
                PeepoPractice.log("Using configured inventory.");
                InventoryUtils.putItems(player.inventory, PeepoPractice.CATEGORY);
            }
            player.getHungerManager().setSaturationLevelClient(10.0F);
        }
    }
}
