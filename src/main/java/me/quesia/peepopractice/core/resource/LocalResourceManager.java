package me.quesia.peepopractice.core.resource;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.resource.provider.CustomDataPackProvider;
import net.minecraft.resource.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ReentrantThreadExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocalResourceManager extends ReentrantThreadExecutor<Runnable> {
    public static LocalResourceManager INSTANCE;
    private final Thread thread;

    public LocalResourceManager(Thread thread) {
        super("LocalResourceManager");
        INSTANCE = this;
        this.thread = thread;
    }

    public CompletableFuture<ServerResourceManager> reload() {
        try (ResourcePackManager<ResourcePackProfile> resourcePackManager = new ResourcePackManager<>(ResourcePackProfile::new, new CustomDataPackProvider())) {
            resourcePackManager.scanPacks();
            List<ResourcePack> packs = resourcePackManager.createResourcePacks();
            return ServerResourceManager.reload(
                    packs,
                    CommandManager.RegistrationEnvironment.INTEGRATED,
                    2,
                    Util.getServerWorkerExecutor(),
                    this
            );
        }
    }

    public void tickTasks() {
        this.runTasks();
        this.runTasks(() -> {
            synchronized (PeepoPractice.SERVER_RESOURCE_MANAGER) {
                return PeepoPractice.SERVER_RESOURCE_MANAGER.get() != null;
            }
        });
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable runnable) {
        synchronized (PeepoPractice.SERVER_RESOURCE_MANAGER) {
            return PeepoPractice.SERVER_RESOURCE_MANAGER.get() == null;
        }
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }
}
