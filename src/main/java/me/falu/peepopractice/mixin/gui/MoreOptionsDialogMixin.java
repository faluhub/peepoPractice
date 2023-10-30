package me.falu.peepopractice.mixin.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.falu.peepopractice.PeepoPractice;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.OptionalLong;
import java.util.Random;

@Mixin(MoreOptionsDialog.class)
public class MoreOptionsDialogMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyArg(
            method = "getGeneratorOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/GeneratorOptions;withHardcore(ZLjava/util/OptionalLong;)Lnet/minecraft/world/gen/GeneratorOptions;",
                    ordinal = 0
            ),
            index = 1
    )
    private OptionalLong peepoPractice$insertSeedList(OptionalLong original) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            if (PeepoPractice.CATEGORY.getWorldProperties().hasSeedListPath()) {
                String seedListPath = PeepoPractice.CATEGORY.getWorldProperties().getSeedListPath();
                try {
                    String content = FileUtils.readFileToString(FabricLoader.getInstance().getConfigDir().resolve(PeepoPractice.MOD_NAME + "/seed_lists/" + seedListPath + ".json").toFile(), Charset.defaultCharset());
                    JsonArray seeds = new JsonParser().parse(content).getAsJsonArray();
                    long seed = seeds.get(new Random().nextInt(seeds.size() - 1)).getAsLong();
                    return OptionalLong.of(seed);
                } catch (IOException e) {
                    PeepoPractice.LOGGER.error("Couldn't load seed list", e);
                }
            }
        }
        return original;
    }
}
