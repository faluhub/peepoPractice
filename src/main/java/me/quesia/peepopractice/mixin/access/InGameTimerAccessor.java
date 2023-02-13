package me.quesia.peepopractice.mixin.access;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InGameTimer.class, remap = false)
public interface InGameTimerAccessor {
    @Accessor("firstInput") String getFirstInput();
}
