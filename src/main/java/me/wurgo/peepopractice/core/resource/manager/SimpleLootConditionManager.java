package me.wurgo.peepopractice.core.resource.manager;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.wurgo.peepopractice.PeepoPractice;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SimpleLootConditionManager extends LootConditionManager {
    private static final Gson GSON = LootGsons.getConditionGsonBuilder().create();
    public Map<Identifier, LootCondition> conditions;

    public void reload(Map<Identifier, JsonElement> map) {
        ImmutableMap.Builder<Identifier, LootCondition> builder = ImmutableMap.builder();
        map.forEach((identifier, jsonElement) -> {
            try {
                if (jsonElement.isJsonArray()) {
                    LootCondition[] lootConditions = GSON.fromJson(jsonElement, LootCondition[].class);
                    builder.put(identifier, new LootConditionManager.AndCondition(lootConditions));
                } else {
                    LootCondition lootCondition = GSON.fromJson(jsonElement, LootCondition.class);
                    builder.put(identifier, lootCondition);
                }
            } catch (Exception var4) {
                PeepoPractice.LOGGER.error("Couldn't parse loot table {}", identifier, var4);
            }
        });
        Map<Identifier, LootCondition> map2 = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, map2::get, (identifier) -> null);
        map2.forEach((identifier, lootCondition) -> lootCondition.validate(lootTableReporter.withCondition("{" + identifier + "}", identifier)));
        lootTableReporter.getMessages().forEach((string, string2) -> PeepoPractice.LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.conditions = map2;
    }
}
