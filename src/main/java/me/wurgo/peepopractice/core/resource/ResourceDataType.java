package me.wurgo.peepopractice.core.resource;

import java.util.Locale;

public enum ResourceDataType {
    LOOT_TABLES,
    PREDICATES,
    RECIPES,
    ADVANCEMENTS;

    public final String value;

    ResourceDataType() {
        this.value = this.name().toLowerCase(Locale.ROOT);
    }
}
