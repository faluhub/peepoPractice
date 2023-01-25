package me.wurgo.peepopractice.core.resource.manager;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.wurgo.peepopractice.PeepoPractice;
import me.wurgo.peepopractice.core.resource.JsonDataReader;
import me.wurgo.peepopractice.core.resource.ResourceDataType;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;

public class SimpleLootManager extends LootManager {
    private static final Gson GSON = LootGsons.getTableGsonBuilder().create();
    private final LootConditionManager conditionManager;
    public ImmutableMap<Identifier, LootTable> tables = ImmutableMap.of();

    public SimpleLootManager(LootConditionManager conditionManager) {
        super(conditionManager);

        this.conditionManager = conditionManager;
    }

    public void reload() {
        Map<Identifier, JsonElement> map = JsonDataReader.prepareData(GSON, ResourceDataType.LOOT_TABLES);

        ImmutableMap.Builder<Identifier, LootTable> builder = ImmutableMap.builder();
        JsonElement jsonElement = map.remove(LootTables.EMPTY);
        if (jsonElement != null) {
            PeepoPractice.LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", LootTables.EMPTY);
        }

        map.forEach((identifier, jsonElementx) -> {
            try {
                LootTable lootTable = GSON.fromJson(jsonElementx, LootTable.class);
                builder.put(identifier, lootTable);
            } catch (Exception var4) {
                PeepoPractice.LOGGER.error("Couldn't parse loot table {}", identifier, var4);
            }
        });

        builder.put(LootTables.EMPTY, LootTable.EMPTY);
        ImmutableMap<Identifier, LootTable> immutableMap = builder.build();
        LootContextType var10002 = LootContextTypes.GENERIC;
        Function<Identifier, LootCondition> var10003 = this.conditionManager::get;
        LootTableReporter lootTableReporter = new LootTableReporter(var10002, var10003, immutableMap::get);
        immutableMap.forEach((identifier, lootTable) -> validate(lootTableReporter, identifier, lootTable));
        lootTableReporter.getMessages().forEach((key, value) -> PeepoPractice.LOGGER.warn("Found validation problem in " + key + ": " + value));
        this.tables = immutableMap;
    }
}
