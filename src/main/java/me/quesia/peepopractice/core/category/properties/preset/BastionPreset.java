package me.quesia.peepopractice.core.category.properties.preset;

import me.quesia.peepopractice.core.NotInitializedException;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.PracticeTypes;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.gen.feature.StructureFeature;

public class BastionPreset {
    public static final PracticeCategory.ExecuteReturnTask<BlockPos> BASTION_SPAWN_POS = (category, random, world) -> {
        if (category.hasCustomValue("bastionType")) {
            PracticeTypes.BastionType type = PracticeTypes.BastionType.fromId((int) category.getCustomValue("bastionType"));
            if (type != null) {
                StructureProperties properties = category.findStructureProperties(StructureFeature.BASTION_REMNANT);
                if (properties != null && properties.getChunkPos() != null) {
                    BlockPos pos = properties.getChunkPos().getCenterBlockPos().add(type.pos);
                    world.getServer().execute(() -> {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
                    });
                    return pos;
                }
            }
        }
        throw new NotInitializedException();
    };
    public static final PracticeCategory.ExecuteReturnTask<Vec2f> BASTION_SPAWN_ANGLE = (category, random, world) -> {
        if (category.hasCustomValue("bastionType")) {
            PracticeTypes.BastionType type = PracticeTypes.BastionType.fromId((int) category.getCustomValue("bastionType"));
            if (type != null) {
                return new Vec2f(type.angle, 0.0F);
            }
        }
        throw new NotInitializedException();
    };
}
