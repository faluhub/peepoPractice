package me.falu.peepopractice.mixin;

import com.google.common.collect.Sets;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerAdvancementTracker;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(value = ClientAdvancementManager.class, priority = 1005 // A higher priority than SRIGT's mixin. Higher = later. My theory is SRIGT is applying something?
)
public abstract class ClientAdvancementManagerMixin {
    @Shadow @Final public Map<Advancement, AdvancementProgress> advancementProgresses;
    @Shadow @Final private AdvancementManager manager;
    @Shadow @Final private MinecraftClient client;

    @Shadow
    public abstract AdvancementManager getManager();

    @ModifyVariable(method = "onAdvancements", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"))
    private Map.Entry<Identifier, AdvancementProgress> peepoPractice$advancement(Map.Entry<Identifier, AdvancementProgress> value) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            Advancement advancement = this.manager.get(value.getKey());
            AdvancementProgress progress = value.getValue();
            if (advancement != null) {
                progress.init(advancement.getCriteria(), advancement.getRequirements());
                if (progress.isDone()) {
                    if (PeepoPractice.CATEGORY.hasSplitEvent()) {
                        if (PeepoPractice.CATEGORY.getSplitEvent() instanceof GetAdvancementSplitEvent) {
                            GetAdvancementSplitEvent event = (GetAdvancementSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                            if (!event.allAdvancements() && advancement.getId().getPath().equals(event.getAdvancement().getPath())) {
                                event.complete(this.client.player != null && !this.client.player.isDead());
                            }
                        }
                    }

                    if (advancement.getId().getPath().equals("nether/find_fortress") && InGameTimer.getInstance().getStatus() != TimerStatus.NONE) {
                        if (InGameTimer.getInstance().getInGameTime() / 1000 <= 180 && PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.NETHER_SPLIT)) {
                            PeepoPractice.CATEGORY.putCustomValue("showPauseBoy", true);
                        }
                    }
                }
            }
        }
        return value;
    }

    @Inject(method = "onAdvancements", at = @At("RETURN"))
    private void peepoPractice$checkAA(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasSplitEvent()) {
            if (PeepoPractice.CATEGORY.getSplitEvent() instanceof GetAdvancementSplitEvent) {
                GetAdvancementSplitEvent event = (GetAdvancementSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                InGameTimer timer = InGameTimer.getInstance();
                int count = this.getCompleteAdvancementsCount();
                int maxCount = timer.getMoreData(7441) == 0 ? 80 : timer.getMoreData(7441);
                if (count >= maxCount - 1) {
                    event.complete(this.client.player != null && !this.client.player.isDead());
                }
            }
        }
    }

    @Unique
    private int getCompleteAdvancementsCount() {
        Set<String> completedAdvancements = Sets.newHashSet();
        for (Map.Entry<String, TimerAdvancementTracker.AdvancementTrack> track : InGameTimer.getInstance().getAdvancementsTracker().getAdvancements().entrySet()) {
            if (track.getValue().isAdvancement() && track.getValue().isComplete()) {
                completedAdvancements.add(track.getKey());
            }
        }
        for (Advancement advancement : this.getManager().getAdvancements()) {
            if (this.advancementProgresses.containsKey(advancement) && advancement.getDisplay() != null) {
                AdvancementProgress advancementProgress = this.advancementProgresses.get(advancement);
                advancementProgress.init(advancement.getCriteria(), advancement.getRequirements());
                String advancementId = advancement.getId().toString();
                if (advancementProgress.isDone() && completedAdvancements.contains(advancementId)) {
                    completedAdvancements.add(advancementId);
                    InGameTimer.getInstance().tryInsertNewAdvancement(advancementId, null, true);
                }
            }
        }
        return completedAdvancements.size();
    }
}
