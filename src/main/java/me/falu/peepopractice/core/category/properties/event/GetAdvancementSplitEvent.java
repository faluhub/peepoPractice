package me.falu.peepopractice.core.category.properties.event;

import net.minecraft.util.Identifier;

public class GetAdvancementSplitEvent extends SplitEvent {
    private Identifier advancement;

    public Identifier getAdvancement() {
        return this.advancement;
    }

    public GetAdvancementSplitEvent setAdvancement(Identifier advancement) {
        this.advancement = advancement;
        return this;
    }

    public boolean allAdvancements() {
        return this.advancement == null;
    }
}
