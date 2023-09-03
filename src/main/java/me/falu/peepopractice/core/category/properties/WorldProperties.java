package me.falu.peepopractice.core.category.properties;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class WorldProperties extends BaseProperties {
    private RegistryKey<World> worldRegistryKey;
    private boolean spawnChunksDisabled = false;
    private String seedListPath = null;
    private final Map<Biome, @Nullable Integer> antiBiomeRangeMap = new HashMap<>();
    private final Map<Biome, Range> proBiomeRangeMap = new HashMap<>();
    private boolean dragonKilled = false;

    public RegistryKey<World> getWorldRegistryKey() {
        return this.worldRegistryKey;
    }

    public boolean hasWorldRegistryKey() {
        return this.worldRegistryKey != null;
    }

    public WorldProperties setWorldRegistryKey(RegistryKey<World> worldRegistryKey) {
        this.worldRegistryKey = worldRegistryKey;
        return this;
    }

    public boolean isSpawnChunksDisabled() {
        return this.spawnChunksDisabled;
    }

    public WorldProperties setSpawnChunksDisabled(boolean spawnChunksDisabled) {
        this.spawnChunksDisabled = spawnChunksDisabled;
        return this;
    }

    public Map<Biome, Integer> getAntiBiomeRangeMap() {
        return this.antiBiomeRangeMap;
    }

    public WorldProperties addAntiBiomeRange(Biome biome, @Nullable Integer range) {
        this.antiBiomeRangeMap.put(biome, range);
        return this;
    }

    public Map<Biome, Range> getProBiomeRangeMap() {
        return this.proBiomeRangeMap;
    }

    public WorldProperties addProBiomeRange(Biome biome, @Nullable Integer range) {
        return this.addProBiomeRange(biome, range, () -> true);
    }

    public WorldProperties addProBiomeRange(Biome biome, @Nullable Integer range, ConditionTask condition) {
        this.proBiomeRangeMap.put(biome, new Range(range, condition));
        return this;
    }

    public String getSeedListPath() {
        return this.seedListPath;
    }

    public boolean hasSeedListPath() {
        return this.seedListPath != null;
    }

    public WorldProperties setSeedList(String seedListPath) {
        this.seedListPath = seedListPath;
        return this;
    }

    public boolean getDragonKilled() {
        return this.dragonKilled;
    }

    public WorldProperties setDragonKilled(boolean dragonKilled) {
        this.dragonKilled = dragonKilled;
        return this;
    }

    public static class Range {
        private final @Nullable Integer range;
        private final ConditionTask condition;

        public Range(@Nullable Integer range, ConditionTask condition) {
            this.range = range;
            this.condition = condition;
        }

        public @Nullable Integer getRange() {
            return this.range;
        }

        public boolean shouldPlace() {
            return this.condition.execute();
        }
    }

    public interface ConditionTask {
        boolean execute();
    }
}
