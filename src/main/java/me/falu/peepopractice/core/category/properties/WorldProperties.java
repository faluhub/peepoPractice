package me.falu.peepopractice.core.category.properties;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class WorldProperties extends BaseProperties {
    private RegistryKey<World> worldRegistryKey = World.OVERWORLD;
    private boolean spawnChunksDisabled = false;
    private String seedListPath;
    private final List<BiomeModification> proBiomes = new ArrayList<>();
    private final List<BiomeModification> antiBiomes = new ArrayList<>();
    private final List<String> dataPacks = new ArrayList<>();
    private Difficulty startDifficulty = Difficulty.EASY;

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

    public List<BiomeModification> getAntiBiomes() {
        return this.antiBiomes;
    }

    public WorldProperties addAntiBiome(BiomeModification antiBiome) {
        this.antiBiomes.add(antiBiome);
        return this;
    }

    public List<BiomeModification> getProBiomes() {
        return this.proBiomes;
    }

    public WorldProperties addProBiome(BiomeModification proBiome) {
        this.proBiomes.add(proBiome);
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

    public WorldProperties useDatapack(String dataPack) {
        this.dataPacks.add(dataPack);
        return this;
    }

    public List<String> getDataPacks() {
        return this.dataPacks;
    }

    public Difficulty getStartDifficulty() {
        return this.startDifficulty;
    }

    public WorldProperties setStartDifficulty(Difficulty startDifficulty) {
        this.startDifficulty = startDifficulty;
        return this;
    }

    public interface ConditionTask {
        boolean execute();
    }

    public static class Range {
        private Integer range;
        private ConditionTask condition = () -> true;
        private final List<RegistryKey<World>> validDimensions = new ArrayList<>();

        public Integer getRange() {
            return this.range;
        }

        public Range setRange(Integer range) {
            this.range = range;
            return this;
        }

        public boolean shouldPlace() {
            return this.condition.execute();
        }

        public Range setCondition(ConditionTask condition) {
            this.condition = condition;
            return this;
        }

        public boolean isValidDimension(RegistryKey<World> dimension) {
            return this.validDimensions.contains(dimension);
        }

        public Range addValidDimension(RegistryKey<World> dimension) {
            this.validDimensions.add(dimension);
            return this;
        }

        public Range addValidDimensions(List<RegistryKey<World>> dimensions) {
            this.validDimensions.addAll(dimensions);
            return this;
        }
    }

    public static class BiomeModification {
        private Biome biome;
        private Range range;
        private Biome replacement;

        public Biome getBiome() {
            return this.biome;
        }

        public boolean hasBiome() {
            return this.biome != null;
        }

        public BiomeModification setBiome(Biome biome) {
            this.biome = biome;
            return this;
        }

        public Range getRange() {
            return this.range;
        }

        public BiomeModification setRange(@Nullable Range range) {
            this.range = range == null ? new Range().setRange(null) : range;
            return this;
        }

        public boolean isInfinite() {
            return this.range.getRange() == null;
        }

        public Biome getReplacement() {
            return this.replacement;
        }

        public boolean hasReplacement() {
            return this.replacement != null;
        }

        public BiomeModification setReplacement(Biome replacement) {
            this.replacement = replacement;
            return this;
        }
    }
}
