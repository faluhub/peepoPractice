package me.falu.peepopractice.mixin.compat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.falu.peepopractice.PeepoPractice;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocateCommand.class)
public abstract class LocateCommandMixin {
    @Unique private static final SimpleCommandExceptionType AFFECTED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("peepopractice.locate_error"));

    @Inject(method = "execute", at = @At("HEAD"))
    private static void peepoPractice$cancelAffectedLocate(ServerCommandSource source, StructureFeature<?> structureFeature, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        if (PeepoPractice.CATEGORY.hasStructureProperties() && PeepoPractice.CATEGORY.findStructureProperties(structureFeature) != null) {
            throw AFFECTED_EXCEPTION.create();
        }
    }
}
