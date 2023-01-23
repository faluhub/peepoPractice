package me.wurgo.peepopractice.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final public GameOptions options;
    @Shadow @Final public WorldRenderer worldRenderer;
    @Shadow @Final public GameRenderer gameRenderer;
    @Shadow @Nullable public abstract Entity getCameraEntity();
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Final public InGameHud inGameHud;
    @Shadow @Nullable public Screen currentScreen;
    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;
    @Shadow @Final private TutorialManager tutorialManager;
    @Shadow public abstract void openScreen(@Nullable Screen screen);
    @Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();
    @Shadow protected abstract void method_29041(String string);
    @Shadow protected abstract void doAttack();
    @Shadow protected abstract void doItemUse();
    @Shadow protected abstract void doItemPick();
    @Shadow private int itemUseCooldown;
    @Shadow @Final public Mouse mouse;
    @Shadow @Nullable public Overlay overlay;
    @Shadow protected int attackCooldown;
    @Shadow @Nullable public HitResult crosshairTarget;
    @Shadow @Nullable public ClientWorld world;
    @Shadow @Final public ParticleManager particleManager;

    /**
     * @author Wurgo
     * @reason .
     */
    @Overwrite
    private void handleInputEvents() {
        for (; this.options.keyTogglePerspective.wasPressed(); this.worldRenderer.scheduleTerrainUpdate()) {
            ++this.options.perspective;
            if (this.options.perspective > 2) {
                this.options.perspective = 0;
            }

            if (this.options.perspective == 0) {
                this.gameRenderer.onCameraEntitySet(this.getCameraEntity());
            } else if (this.options.perspective == 1) {
                this.gameRenderer.onCameraEntitySet(null);
            }
        }

        while (this.options.keySmoothCamera.wasPressed()) {
            this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
        }

        for (int i = 0; i < 9; ++i) {
            boolean bl = this.options.keySaveToolbarActivator.isPressed();
            boolean bl2 = this.options.keyLoadToolbarActivator.isPressed();
            if (this.options.keysHotbar[i].wasPressed()) {
                if (this.player != null && this.player.isSpectator()) {
                    this.inGameHud.getSpectatorHud().selectSlot(i);
                } else if (this.player != null && !this.player.isCreative() || this.currentScreen != null || !bl2 && !bl) {
                    this.player.inventory.selectedSlot = i;
                } else {
                    CreativeInventoryScreen.onHotbarKeyPress((MinecraftClient) (Object) this, i, bl2, bl);
                }
            }
        }

        while (this.options.keyInventory.wasPressed()) {
            if (this.player != null) {
                if (this.interactionManager.hasRidingInventory()) {
                    this.player.openRidingInventory();
                } else {
                    this.tutorialManager.onInventoryOpened();
                    this.openScreen(new InventoryScreen(this.player));
                }
            }
        }

        while (this.options.keyAdvancements.wasPressed()) {
            if (this.player != null) {
                this.openScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
            }
        }

        while (this.options.keySwapHands.wasPressed()) {
            if (this.player != null && !this.player.isSpectator()) {
                this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
            }
        }

        while (this.options.keyDrop.wasPressed()) {
            if (this.player != null && !this.player.isSpectator() && this.player.dropSelectedItem(Screen.hasControlDown())) {
                this.player.swingHand(Hand.MAIN_HAND);
            }
        }

        boolean bl3 = this.options.chatVisibility != ChatVisibility.HIDDEN;
        if (bl3) {
            while (this.options.keyChat.wasPressed()) {
                this.method_29041("");
            }

            if (this.currentScreen == null && this.overlay == null && this.options.keyCommand.wasPressed()) {
                this.method_29041("/");
            }
        }

        if (this.player != null && this.player.isUsingItem()) {
            if (!this.options.keyUse.isPressed() && this.interactionManager != null) {
                this.interactionManager.stopUsingItem(this.player);
            }

            label113:
            while (true) {
                if (!this.options.keyAttack.wasPressed()) {
                    while (true) {
                        if (this.options.keyPickItem.wasPressed()) {
                            continue;
                        }
                        break label113;
                    }
                }
            }
        } else {
            while (this.options.keyAttack.wasPressed()) {
                this.doAttack();
            }

            while (this.options.keyUse.wasPressed()) {
                this.doItemUse();
            }

            while (this.options.keyPickItem.wasPressed()) {
                this.doItemPick();
            }
        }

        if (this.player != null) {
            if (this.options.keyUse.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
                this.doItemUse();
            }
        }

        this.handleBlockBreaking(this.currentScreen == null && this.options.keyAttack.isPressed() && this.mouse.isCursorLocked());
    }

    /**
     * @author Wurgo
     * @reason .
     */
    @Overwrite
    private void handleBlockBreaking(boolean bl) {
        if (!bl) {
            this.attackCooldown = 0;
        }

        if (this.player != null) {
            if (this.attackCooldown <= 0 && !this.player.isUsingItem() && this.interactionManager != null) {
                if (bl && this.crosshairTarget != null && this.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    if (this.world != null && !this.world.getBlockState(blockPos).isAir()) {
                        Direction direction = blockHitResult.getSide();
                        if (this.interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
                            this.particleManager.addBlockBreakingParticles(blockPos, direction);
                            this.player.swingHand(Hand.MAIN_HAND);
                        }
                    }
                } else {
                    this.interactionManager.cancelBlockBreaking();
                }
            }
        }
    }
}
