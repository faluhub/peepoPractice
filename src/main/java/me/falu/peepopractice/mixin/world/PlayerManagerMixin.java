package me.falu.peepopractice.mixin.world;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.properties.PlayerProperties;
import me.falu.peepopractice.core.category.utils.InventoryUtils;
import me.falu.peepopractice.core.exception.NotInitializedException;
import me.falu.peepopractice.core.global.GlobalOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @ModifyVariable(method = "onPlayerConnect", at = @At("STORE"))
    private RegistryKey<World> peepoPractice$otherDimension(RegistryKey<World> value) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) {
            return PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey();
        }
        return value;
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void peepoPractice$setInventory(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) throws NotInitializedException {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.inGameHud.setTitles(null, null, -1, -1, -1);
            if (!PeepoPractice.CATEGORY.isAA() && GlobalOptions.SAME_INVENTORY.get(client.options) && !InventoryUtils.PREVIOUS_INVENTORY.isEmpty()) {
                PeepoPractice.log("Using inventory from previous split.");
                player.inventory.clear();
                for (int i = 0; i < InventoryUtils.PREVIOUS_INVENTORY.size(); i++) {
                    player.inventory.setStack(i, InventoryUtils.PREVIOUS_INVENTORY.get(i).copy());
                }
            } else {
                PeepoPractice.log("Using configured inventory.");
                InventoryUtils.putItems(player.inventory, PeepoPractice.CATEGORY, InventoryUtils.getSelectedInventory());
                if (CategoryPreferences.SCRAMBLE_INVENTORY.getBoolValue()) {
                    PeepoPractice.log("Scrambling inventory.");
                    List<Integer> taken = new ArrayList<>();
                    DefaultedList<ItemStack> newInventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
                    for (int i = 0; i < player.inventory.main.size() - 9; i++) {
                        int newIndex = -1;
                        while (newIndex == -1 || taken.contains(newIndex)) {
                            newIndex = 9 + player.getRandom().nextInt(player.inventory.main.size() - 9);
                        }
                        taken.add(newIndex);
                        newInventory.set(newIndex, player.inventory.main.get(9 + i));
                    }
                    for (int i = 0; i < newInventory.size() - 9; i++) {
                        player.inventory.main.set(i + 9, newInventory.get(i + 9));
                    }
                }
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
                                        public void sendSystemMessage(Text message, UUID senderUuid) { }

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
                for (PlayerProperties.PotionEffect potionEffect : PeepoPractice.CATEGORY.getPlayerProperties().getPotionEffects()) {
                    if (potionEffect.hasEffect()) {
                        if (!potionEffect.hasCondition() || Boolean.TRUE.equals(potionEffect.getCondition().execute(PeepoPractice.CATEGORY, new Random(player.getServerWorld().getSeed()), player.getServerWorld()))) {
                            player.addStatusEffect(new StatusEffectInstance(potionEffect.getEffect(), potionEffect.getDuration(), potionEffect.getAmplifier()));
                        }
                    }
                }
            }
            player.getRecipeBook().setGuiOpen(InventoryUtils.BOOK_OPEN);
            player.getRecipeBook().setFilteringCraftable(InventoryUtils.FILTERING_CRAFTABLE);
        }
    }
}
