package me.falu.peepopractice.core.category.properties.event;

import lombok.Getter;
import net.minecraft.util.Identifier;

@Getter
public class GetAdvancementSplitEvent extends SplitEvent {
    private Identifier advancement;

    public GetAdvancementSplitEvent setAdvancement(Identifier advancement) {
        this.advancement = advancement;
        return this;
    }

    public boolean allAdvancements() {
        return this.advancement == null;
    }
}
