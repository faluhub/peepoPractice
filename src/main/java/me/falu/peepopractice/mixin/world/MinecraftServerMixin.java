package me.falu.peepopractice.mixin.world;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategoriesAny;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.exception.NotInitializedException;
import me.falu.peepopractice.owner.GenerationShutdownOwner;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.*;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements GenerationShutdownOwner {
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final protected SaveProperties saveProperties;
    @Shadow @Final protected RegistryTracker.Modifiable dimensionTracker;
    @Shadow @Final protected LevelStorage.Session session;
    @Shadow @Final private Executor workerExecutor;
    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;
    @Shadow private @Nullable DataCommandStorage dataCommandStorage;
    @Shadow @Final private Snooper snooper;
    @Shadow private ServerResourceManager serverResourceManager;

    @SuppressWarnings("SameParameterValue")
    @Shadow
    private static void setupSpawn(ServerWorld serverWorld, ServerWorldProperties serverWorldProperties, boolean bl, boolean bl2, boolean bl3) { throw new UnsupportedOperationException(); }

    @ModifyExpressionValue(method = "setupSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeSource;getSpawnBiomes()Ljava/util/List;"))
    private static List<Biome> peepoPractice$addOceanSpawnBiome(List<Biome> spawnBiomes) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            PeepoPractice.CATEGORY.getWorldProperties().getProBiomes().forEach(v -> {
                if (BiomeLayers.isOcean(Registry.BIOME.getRawId(v.getBiome()))) {
                    spawnBiomes.add(v.getBiome());
                }
            });
        }
        return spawnBiomes;
    }

    @Shadow
    protected abstract void initScoreboard(PersistentStateManager persistentStateManager);
    @Shadow
    protected abstract void setToDebugWorldProperties(SaveProperties properties);
    @Shadow
    public abstract PlayerManager getPlayerManager();
    @Shadow
    public abstract BossBarManager getBossBarManager();
    @Shadow
    public abstract ServerWorld getOverworld();
    @SuppressWarnings("UnusedReturnValue")
    @Shadow
    public abstract boolean save(boolean bl, boolean bl2, boolean bl3);
    @Shadow
    protected abstract void method_16208();
    @Shadow
    @Nullable
    public abstract ServerNetworkIo getNetworkIo();
    @Shadow
    public abstract Iterable<ServerWorld> getWorlds();
    @Shadow
    protected abstract void setFavicon(ServerMetadata metadata);

    /**
     * @author falu, contaria
     * @reason Custom start dimension
     */
    @Inject(method = "createWorlds", at = @At("HEAD"), cancellable = true)
    private void peepoPractice$createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.hasWorldProperties() || !PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) {
            return;
        }
        ci.cancel();

        ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
        GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
        boolean bl = generatorOptions.isDebugWorld();
        long l = generatorOptions.getSeed();
        long m = BiomeAccess.hashSeed(l);
        ImmutableList<Spawner> list = ImmutableList.of(new PhantomSpawner(), new PillagerSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverWorldProperties));
        SimpleRegistry<DimensionOptions> simpleRegistry = generatorOptions.getDimensionMap();
        DimensionOptions dimensionOptions = simpleRegistry.get(RegistryKey.of(Registry.DIMENSION_OPTIONS, PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().getValue()));
        if (dimensionOptions == null) {
            return;
        }
        DimensionType dimensionType = dimensionOptions.getDimensionType();
        ChunkGenerator chunkGenerator = dimensionOptions.getChunkGenerator();
        RegistryKey<DimensionType> registryKey = this.dimensionTracker.getDimensionTypeRegistry().getKey(dimensionType).orElseThrow(() -> new IllegalStateException("Unregistered dimension type: " + dimensionType));
        ServerWorld serverWorld = new ServerWorld((MinecraftServer) (Object) this, this.workerExecutor, this.session, serverWorldProperties, PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey(), registryKey, dimensionType, worldGenerationProgressListener, chunkGenerator, bl, m, PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().equals(World.OVERWORLD) ? list : ImmutableList.of(), true);

        boolean initializedStructures = false;
        PeepoPractice.CATEGORY.reset();
        if (PeepoPractice.CATEGORY.hasPlayerProperties()) {
            try {
                PeepoPractice.CATEGORY.getPlayerProperties().reset(new Random(chunkGenerator.field_24748), serverWorld);
            } catch (NotInitializedException ignored) {
                try {
                    for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
                        properties.reset(new Random(chunkGenerator.field_24748), serverWorld);
                    }
                    initializedStructures = true;
                    PeepoPractice.CATEGORY.getPlayerProperties().reset(new Random(chunkGenerator.field_24748), serverWorld);
                } catch (NotInitializedException ignored1) {
                    PeepoPractice.log(PeepoPractice.CATEGORY.getId() + " in an infinite loop, retrying at spawn. (Occurrence 1)");
                    PeepoPractice.RETRY_PLAYER_INITIALIZATION = true;
                }
            }
        }
        if (!initializedStructures) {
            for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
                try {
                    properties.reset(new Random(chunkGenerator.field_24748), serverWorld);
                } catch (NotInitializedException ignored) {
                    PeepoPractice.log(PeepoPractice.CATEGORY.getId() + " in an infinite loop. (Occurrence 2)");
                }
            }
        }

        this.worlds.put(PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey(), serverWorld);
        PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();
        this.initScoreboard(persistentStateManager);
        this.dataCommandStorage = new DataCommandStorage(persistentStateManager);
        WorldBorder worldBorder = serverWorld.getWorldBorder();
        worldBorder.load(serverWorldProperties.getWorldBorder());
        if (!serverWorldProperties.isInitialized()) {
            try {
                setupSpawn(serverWorld, serverWorldProperties, generatorOptions.hasBonusChest(), bl, true);
                serverWorldProperties.setInitialized(true);
                if (bl) {
                    this.setToDebugWorldProperties(this.saveProperties);
                }
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Exception initializing level");
                try {
                    serverWorld.addDetailsToCrashReport(crashReport);
                } catch (Throwable ignored) {
                }
                throw new CrashException(crashReport);
            }
            serverWorldProperties.setInitialized(true);
        }
        this.getPlayerManager().setMainWorld(serverWorld);
        if (this.saveProperties.getCustomBossEvents() != null) {
            this.getBossBarManager().fromTag(this.saveProperties.getCustomBossEvents());
        }
        for (Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : simpleRegistry.getEntries()) {
            RegistryKey<DimensionOptions> registryKey2 = entry.getKey();
            if (registryKey2.getValue() == PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().getValue()) {
                continue;
            }
            RegistryKey<World> registryKey3 = RegistryKey.of(Registry.DIMENSION, registryKey2.getValue());
            DimensionType dimensionType2 = entry.getValue().getDimensionType();
            RegistryKey<DimensionType> registryKey4 = this.dimensionTracker.getDimensionTypeRegistry().getKey(dimensionType2).orElseThrow(() -> new IllegalStateException("Unregistered dimension type: " + dimensionType2));
            ChunkGenerator chunkGenerator2 = entry.getValue().getChunkGenerator();
            UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(this.saveProperties, serverWorldProperties);
            ServerWorld serverWorld2 = new ServerWorld((MinecraftServer) (Object) this, this.workerExecutor, this.session, unmodifiableLevelProperties, registryKey3, registryKey4, dimensionType2, worldGenerationProgressListener, chunkGenerator2, bl, m, ImmutableList.of(), false);
            worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(serverWorld2.getWorldBorder()));
            this.worlds.put(registryKey3, serverWorld2);
        }
    }

    @ModifyReturnValue(method = "getOverworld", at = @At("RETURN"))
    private ServerWorld peepoPractice$customSpawnDimension(ServerWorld world) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) {
            return this.worlds.get(PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey());
        }
        return world;
    }

    @Inject(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J", ordinal = 2))
    private void peepoPractice$removeTicketsAfterGen(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().isSpawnChunksDisabled()) {
            BlockPos blockPos = this.getOverworld().getSpawnPos();
            if (PeepoPractice.CATEGORY.hasPlayerProperties() && PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnPos()) {
                blockPos = PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos();
            }
            this.getOverworld().getChunkManager().removeTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);
            this.save(true, true, false);
            this.method_16208();
        }
    }

    @ModifyReturnValue(method = "getSpawnRadius", at = @At("RETURN"))
    private int peepoPractice$removeSpawnRadius(int spawnRadius) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategoriesAny.EMPTY)) {
            return 0;
        }
        return spawnRadius;
    }

    @Override
    public void peepoPractice$shutdown() {
        LOGGER.info("Stopping server");
        if (this.getNetworkIo() != null) {
            this.getNetworkIo().stop();
        }
        for (ServerWorld world : this.getWorlds()) {
            world.savingDisabled = false;
            ((GenerationShutdownOwner) world.getChunkManager().threadedAnvilChunkStorage).peepoPractice$shutdown();
        }
        if (this.snooper.isActive()) {
            this.snooper.cancel();
        }
        this.serverResourceManager.close();
        try {
            this.session.close();
        } catch (IOException e) {
            LOGGER.error("Failed to unlock level {}", this.session.getDirectoryName(), e);
        }
    }

    @Redirect(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V"))
    private void peepoPractice$preventIconCrash(MinecraftServer instance, ServerMetadata metadata) {
        try {
            this.setFavicon(metadata);
        } catch (IllegalStateException ignored) {
            PeepoPractice.LOGGER.error("Failed to update metadata icon. Prevented crash.");
        }
    }
}
