package me.falu.peepopractice.mixin;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.properties.event.GetAdvancementSplitEvent;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
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
public abstract class ClientAdvancementManagerMixin {
    @Shadow @Final private AdvancementManager manager;
    @Shadow @Final private MinecraftClient client;

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
                            if (event.allAdvancements()) {
                                boolean completedAll = true;
                                for (String advancementKey : PracticeCategoryUtils.ADVANCEMENTS) {
                                    Advancement advancement1 = this.manager.get(new Identifier(advancementKey));
                                    if (advancement1 != null) {
                                        AdvancementProgress progress1 = new AdvancementProgress();
                                        progress1.init(advancement1.getCriteria(), advancement1.getRequirements());
                                        if (!progress1.isDone()) {
                                            completedAll = false;
                                            break;
                                        }
                                    }
                                }
                                if (completedAll) {
                                    event.complete(this.client.player != null && !this.client.player.isDead());
                                }
                            } else if (advancement.getId().getPath().equals(event.getAdvancement().getPath())) {
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
}
