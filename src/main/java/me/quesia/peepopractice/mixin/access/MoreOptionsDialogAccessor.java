package me.quesia.peepopractice.mixin.access;

import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MoreOptionsDialog.class)
public interface MoreOptionsDialogAccessor {
    @Accessor("seedText") void setSeedText(String seedText);
}
