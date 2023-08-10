package me.falu.peepopractice.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import me.falu.peepopractice.PeepoPractice;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V", remap = false))
    private boolean peepoPractice$cancelAmbiguities(CommandDispatcher<?> dispatcher, AmbiguityConsumer<ServerCommandSource> consumer) {
        return PeepoPractice.SERVER_RESOURCE_MANAGER.get() != null;
    }
}
