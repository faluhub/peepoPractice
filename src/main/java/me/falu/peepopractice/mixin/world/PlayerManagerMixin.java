package me.falu.peepopractice.mixin.world;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.utils.InventoryUtils;
import me.falu.peepopractice.core.global.GlobalOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow public abstract MinecraftServer getServer();

    @ModifyVariable(method = "onPlayerConnect", at = @At("STORE"))
    private RegistryKey<World> peepoPractice$otherDimension(RegistryKey<World> value) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) {
            return PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey();
        }
        return value;
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void peepoPractice$setInventory(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.inGameHud.setTitles(null, null, -1, -1, -1);
            if (GlobalOptions.SAME_INVENTORY.get(client.options) && !InventoryUtils.PREVIOUS_INVENTORY.isEmpty()) {
                PeepoPractice.log("Using inventory from previous split.");
                player.inventory.clear();
                for (int i = 0; i < InventoryUtils.PREVIOUS_INVENTORY.size(); i++) {
                    player.inventory.setStack(i, InventoryUtils.PREVIOUS_INVENTORY.get(i).copy());
                }
            } else {
                PeepoPractice.log("Using configured inventory.");
                InventoryUtils.putItems(player.inventory, PeepoPractice.CATEGORY);
            }
            if (GlobalOptions.GIVE_SATURATION.get(client.options)) {
                player.getHungerManager().setSaturationLevelClient(10.0F);
            }
            if (PeepoPractice.CATEGORY.hasPlayerProperties()) {
                for (String command : PeepoPractice.CATEGORY.getPlayerProperties().getCommands()) {
                    this.getServer().getCommandManager().execute(
                            new ServerCommandSource(
                                    new CommandOutput() {
                                        @Override
                                        public void sendSystemMessage(Text message, UUID senderUuid) {}

                                        @Override
                                        public boolean shouldReceiveFeedback() {
                                            return false;
                                        }

                                        @Override
                                        public boolean shouldTrackOutput() {
                                            return false;
                                        }

                                        @Override
                                        public boolean shouldBroadcastConsoleToOps() {
                                            return false;
                                        }
                                    },
                                    player.getPos(),
                                    player.getRotationClient(),
                                    player.getServerWorld(),
                                    player.getPermissionLevel(),
                                    player.getName().getString(),
                                    player.getDisplayName(),
                                    this.getServer(),
                                    player
                            ),
                            command
                    );
                }
            }
        }
    }
}
