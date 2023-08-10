package me.falu.peepopractice.mixin.structure;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolRegistry;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(StructurePoolBasedGenerator.class)
public abstract class StructurePoolBasedGeneratorMixin {
    @Shadow @Final public static StructurePoolRegistry REGISTRY;
    private static StructurePieceType TYPE;

    @Inject(method = "addPieces", at = @At("HEAD"))
    private static void peepoPractice$capturePieceType(Identifier startPoolId, int size, StructurePoolBasedGenerator.PieceFactory pieceFactory, ChunkGenerator chunkGenerator, StructureManager structureManager, BlockPos blockPos, List<? super PoolStructurePiece> list, Random random, boolean bl, boolean bl2, CallbackInfo ci) {
        StructurePool structurePool = REGISTRY.get(startPoolId);
        StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
        PoolStructurePiece poolStructurePiece = pieceFactory.create(structureManager, structurePoolElement, blockPos, structurePoolElement.getGroundLevelDelta(), BlockRotation.NONE, structurePoolElement.getBoundingBox(structureManager, blockPos, BlockRotation.NONE));
        TYPE = poolStructurePiece.getType();
    }

    @WrapOperation(method = "addPieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/BlockRotation;random(Ljava/util/Random;)Lnet/minecraft/util/BlockRotation;"))
    private static BlockRotation peepoPractice$setOrientation(Random random, Operation<BlockRotation> original) {
        for (StructureProperties properties : PeepoPractice.CATEGORY.getStructureProperties()) {
            if (properties.hasRotation()) {
                if (TYPE != null) {
                    boolean valid = false;

                    // trust me it's not hardcoded COPIUM it's just the 3 things that StructurePoolBasedGenerator mentions in its class soooo...
                    if (TYPE.equals(StructurePieceType.BASTION_REMNANT) && properties.isSameStructure(StructureFeature.BASTION_REMNANT)) {
                        valid = true;
                    } else if (TYPE.equals(StructurePieceType.VILLAGE) && properties.isSameStructure(StructureFeature.VILLAGE)) {
                        valid = true;
                    } else if (TYPE.equals(StructurePieceType.PILLAGER_OUTPOST) && properties.isSameStructure(StructureFeature.PILLAGER_OUTPOST)) {
                        valid = true;
                    }

                    if (valid) {
                        TYPE = null;
                        return properties.getRotation();
                    }
                }
            }
        }
        return original.call(random);
    }
}
