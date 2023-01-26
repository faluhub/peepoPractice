package me.quesia.peepopractice.core.resource.provider;

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;

import java.util.function.Consumer;

public class CustomDataPackProvider implements ResourcePackProvider {
    private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

    @Override
    public <T extends ResourcePackProfile> void register(Consumer<T> consumer, ResourcePackProfile.Factory<T> factory) {
        T resourcePackProfile = ResourcePackProfile.of("vanilla", true, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN);
        if (resourcePackProfile != null) {
            consumer.accept(resourcePackProfile);
        }
    }
}
