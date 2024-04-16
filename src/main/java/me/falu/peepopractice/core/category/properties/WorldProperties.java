package me.falu.peepopractice.core.category.properties;

import lombok.Getter;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("UnusedDeclaration")
public class WorldProperties extends BaseProperties {
    private final List<BiomeModification> proBiomes = new ArrayList<>();
    private final List<BiomeModification> antiBiomes = new ArrayList<>();
    private final List<String> dataPacks = new ArrayList<>();
    private RegistryKey<World> worldRegistryKey = World.OVERWORLD;
    private boolean spawnChunksDisabled = false;
    private String seedListPath;
    private Difficulty startDifficulty = Difficulty.EASY;

    public WorldProperties setWorldRegistryKey(RegistryKey<World> worldRegistryKey) {
        this.worldRegistryKey = worldRegistryKey;
        return this;
    }

    public boolean hasWorldRegistryKey() {
        return this.worldRegistryKey != null;
    }

    public WorldProperties setSpawnChunksDisabled(boolean spawnChunksDisabled) {
        this.spawnChunksDisabled = spawnChunksDisabled;
        return this;
    }

    public WorldProperties addAntiBiome(BiomeModification antiBiome) {
        this.antiBiomes.add(antiBiome);
        return this;
    }

    public WorldProperties addProBiome(BiomeModification proBiome) {
        this.proBiomes.add(proBiome);
        return this;
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

    public WorldProperties setStartDifficulty(Difficulty startDifficulty) {
        this.startDifficulty = startDifficulty;
        return this;
    }

    public interface ConditionTask {
        boolean execute();
    }

    public static class Range {
        private final List<RegistryKey<World>> validDimensions = new ArrayList<>();
        @Getter private Integer range;
        private ConditionTask condition = () -> true;

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

    @Getter
    public static class BiomeModification {
        private Biome biome;
        private Range range;
        private Biome replacement;

        public BiomeModification setBiome(Biome biome) {
            this.biome = biome;
            return this;
        }

        public boolean hasBiome() {
            return this.biome != null;
        }

        public BiomeModification setRange(@Nullable Range range) {
            this.range = range == null ? new Range().setRange(null) : range;
            return this;
        }

        public boolean isInfinite() {
            return this.range.getRange() == null;
        }

        public BiomeModification setReplacement(Biome replacement) {
            this.replacement = replacement;
            return this;
        }

        public boolean hasReplacement() {
            return this.replacement != null;
        }
    }
}
