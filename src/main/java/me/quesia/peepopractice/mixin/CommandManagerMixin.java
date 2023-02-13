package me.quesia.peepopractice.mixin;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), remap = false)
    private void cancelAmbiguities(CommandDispatcher<?> instance, AmbiguityConsumer<ServerCommandSource> consumer) {
        if (PeepoPractice.SERVER_RESOURCE_MANAGER.get() == null) { return; }
        this.dispatcher.findAmbiguities(consumer);
    }
}
