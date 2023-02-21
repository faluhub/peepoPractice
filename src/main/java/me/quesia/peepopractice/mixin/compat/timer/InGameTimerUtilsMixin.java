package me.quesia.peepopractice.mixin.compat.timer;

import com.redlimerl.speedrunigt.timer.InGameTimerUtils;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.file.Path;

@Mixin(value = InGameTimerUtils.class, remap = false)
public class InGameTimerUtilsMixin {
    @Redirect(method = "getTimerLogDir", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;", ordinal = 0))
    private static Path customSavesDirectory(Path instance, String other) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            return PeepoPractice.PRACTICE_LEVEL_STORAGE.getSavesDirectory();
        }
        return instance;
    }
}
