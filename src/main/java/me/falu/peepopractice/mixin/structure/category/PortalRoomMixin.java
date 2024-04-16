package me.falu.peepopractice.mixin.structure.category;

import me.falu.peepopractice.core.category.CategoryPreference;
import me.falu.peepopractice.core.category.PracticeTypes;
import net.minecraft.structure.StrongholdGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StrongholdGenerator.PortalRoom.class)
public abstract class PortalRoomMixin {
    @ModifyConstant(method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z", constant = @Constant(floatValue = 0.9F))
    private float peepoPractice$setEyeCount(float constant) {
        PracticeTypes.EyeCountType eyeCountType = PracticeTypes.getTypeValue("eye_count", PracticeTypes.EyeCountType.RANDOM);
        switch (eyeCountType) {
            case ALL:
                return 0.0F;
            case RANDOM:
                return constant;
            case NONE:
                return 1.1F;
        }
        return constant;
    }
}
