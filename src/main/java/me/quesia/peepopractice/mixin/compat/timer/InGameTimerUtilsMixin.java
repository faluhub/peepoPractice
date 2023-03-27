package me.quesia.peepopractice.mixin.compat.timer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.redlimerl.speedrunigt.timer.InGameTimerUtils;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.nio.file.Path;

@Mixin(value = InGameTimerUtils.class, remap = false)
public abstract class InGameTimerUtilsMixin {
    @ModifyExpressionValue(
            method = "getTimerLogDir",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=saves"
                    )
            )
    )
    private static Path peepoPractice$customSavesDirectory(Path path) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            return PeepoPractice.PRACTICE_LEVEL_STORAGE.getSavesDirectory();
        }
        return path;
    }
}
