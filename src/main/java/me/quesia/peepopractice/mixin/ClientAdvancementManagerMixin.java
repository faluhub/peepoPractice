package me.quesia.peepopractice.mixin;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
    @Shadow @Final private AdvancementManager manager;
    @Shadow @Final private MinecraftClient client;

    @ModifyVariable(method = "onAdvancements", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"))
    public Map.Entry<Identifier, AdvancementProgress> advancement(Map.Entry<Identifier, AdvancementProgress> value) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            Advancement advancement = this.manager.get(value.getKey());
            AdvancementProgress advancementProgress = value.getValue();
            if (advancement != null) {
                advancementProgress.init(advancement.getCriteria(), advancement.getRequirements());
                if (advancementProgress.isDone()) {
                    if (PeepoPractice.CATEGORY.hasSplitEvent()) {
                        if (PeepoPractice.CATEGORY.getSplitEvent() instanceof GetAdvancementSplitEvent) {
                            GetAdvancementSplitEvent event = (GetAdvancementSplitEvent) PeepoPractice.CATEGORY.getSplitEvent();
                            if (event.hasAdvancement() && advancement.getId().getPath().equals(event.getAdvancement().getPath())) {
                                event.complete(this.client.player != null && !this.client.player.isDead());
                            }
                        }
                    }

                    if (advancement.getId().getPath().equals("nether/find_fortress") && InGameTimer.getInstance().getStatus() != TimerStatus.NONE) {
                        if (InGameTimer.getInstance().getInGameTime() / 1000 <= 240 && PeepoPractice.CATEGORY.equals(PracticeCategories.NETHER_SPLIT)) {
                            PeepoPractice.CATEGORY.putCustomValue("showPauseBoy", true);
                        }
                    }
                }
            }
        }
        return value;
    }
}