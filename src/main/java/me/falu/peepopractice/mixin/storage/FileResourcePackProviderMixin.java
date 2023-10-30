package me.falu.peepopractice.mixin.storage;

import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.properties.WorldProperties;
import me.falu.peepopractice.core.writer.DefaultFileWriter;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(FileResourcePackProvider.class)
public abstract class FileResourcePackProviderMixin {
    @Shadow protected abstract Supplier<ResourcePack> createResourcePack(File file);
    @Shadow @Final private ResourcePackSource field_25345;

    @Inject(method = "register", at = @At("TAIL"))
    private <T extends ResourcePackProfile> void peepoPractice$addCustomDataPacks(Consumer<T> consumer, ResourcePackProfile.Factory<T> factory, CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            WorldProperties props = PeepoPractice.CATEGORY.getWorldProperties();
            for (String dataPack : props.getDataPacks()) {
                String string = "peepoPractice/" + dataPack;
                String suffix = ".zip";
                File file = DefaultFileWriter.INSTANCE.getResourceAsFile("datapacks/" + dataPack + suffix, dataPack + suffix);
                T profile = ResourcePackProfile.of(string, true, this.createResourcePack(file), factory, ResourcePackProfile.InsertionPosition.TOP, this.field_25345);
                if (profile == null) { continue; }
                consumer.accept(profile);
            }
        }
    }
}
