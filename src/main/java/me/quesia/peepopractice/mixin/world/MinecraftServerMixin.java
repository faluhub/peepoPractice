package me.quesia.peepopractice.mixin.world;

import com.google.common.collect.ImmutableList;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import me.quesia.peepopractice.mixin.access.ChunkGeneratorAccessor;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow @Final protected SaveProperties saveProperties;
    @Shadow @Final protected RegistryTracker.Modifiable dimensionTracker;
    @Shadow @Final private Executor workerExecutor;
    @Shadow @Final protected LevelStorage.Session session;
    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;
    @Shadow protected abstract void initScoreboard(PersistentStateManager persistentStateManager);
    @SuppressWarnings("unused") @Shadow private @Nullable DataCommandStorage dataCommandStorage;
    @SuppressWarnings("SameParameterValue") @Shadow private static void setupSpawn(ServerWorld serverWorld, ServerWorldProperties serverWorldProperties, boolean bl, boolean bl2, boolean bl3) { throw new UnsupportedOperationException(); }
    @Shadow protected abstract void setToDebugWorldProperties(SaveProperties properties);
    @Shadow public abstract PlayerManager getPlayerManager();
    @Shadow public abstract BossBarManager getBossBarManager();

    /**
     * @author Quesia
     * @reason Custom start dimension
     */
    @Inject(method = "createWorlds", at = @At("HEAD"), cancellable = true)
    private void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.hasWorldProperties() || !PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) { return; }
        ci.cancel();

        ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
        GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
        boolean bl = generatorOptions.isDebugWorld();
        long l = generatorOptions.getSeed();
        long m = BiomeAccess.hashSeed(l);
        ImmutableList<Spawner> list = ImmutableList.of(new PhantomSpawner(), new PillagerSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverWorldProperties));
        SimpleRegistry<DimensionOptions> simpleRegistry = generatorOptions.getDimensionMap();
        DimensionOptions dimensionOptions = simpleRegistry.get(RegistryKey.of(Registry.DIMENSION_OPTIONS, PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().getValue()));
        if (dimensionOptions == null) { return; }
        DimensionType dimensionType = dimensionOptions.getDimensionType();
        ChunkGenerator chunkGenerator = dimensionOptions.getChunkGenerator();
        RegistryKey<DimensionType> registryKey = this.dimensionTracker.getDimensionTypeRegistry().getKey(dimensionType).orElseThrow(() -> new IllegalStateException("Unregistered dimension type: " + dimensionType));
        ServerWorld serverWorld = new ServerWorld((MinecraftServer) (Object) this, this.workerExecutor, this.session, serverWorldProperties, PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey(), registryKey, dimensionType, worldGenerationProgressListener, chunkGenerator, bl, m, PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().equals(World.OVERWORLD) ? list : ImmutableList.of(), true);

        Random random = new Random(((ChunkGeneratorAccessor) chunkGenerator).getField_24748());
        for (PracticeCategory category : PracticeCategories.ALL) {
            for (StructureProperties properties : category.getStructureProperties()) {
                properties.reset(random, serverWorld);
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
                try { serverWorld.addDetailsToCrashReport(crashReport); }
                catch (Throwable ignored) {}
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
            if (registryKey2.getValue() == PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey().getValue()) continue;
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

    @Inject(method = "getOverworld", at = @At("RETURN"), cancellable = true)
    private void customSpawnDimension(CallbackInfoReturnable<ServerWorld> cir) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) {
            cir.setReturnValue(this.worlds.get(PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey()));
        }
    }

    @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getSpawnPos()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos customSpawnPos(ServerWorld instance) {
        if (PeepoPractice.CATEGORY.hasPlayerProperties() && PeepoPractice.CATEGORY.getPlayerProperties().hasSpawnPos()) {
            return PeepoPractice.CATEGORY.getPlayerProperties().getSpawnPos();
        }
        return instance.getSpawnPos();
    }
}
