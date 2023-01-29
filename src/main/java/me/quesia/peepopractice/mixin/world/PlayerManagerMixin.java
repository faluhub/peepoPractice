package me.quesia.peepopractice.mixin.world;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.InventoryUtils;
import me.quesia.peepopractice.core.PracticeWriter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
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
public class PlayerManagerMixin {
    @ModifyVariable(method = "onPlayerConnect", at = @At("STORE"))
    private RegistryKey<World> otherDimension(RegistryKey<World> value) {
        if (PeepoPractice.CATEGORY != null && PeepoPractice.CATEGORY.getWorldRegistryKey() != null) {
            return PeepoPractice.CATEGORY.getWorldRegistryKey();
        }
        return value;
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void setInventory(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY == null) { return; }
        InventoryUtils.putItems(player.inventory, PeepoPractice.CATEGORY);
    }
}
