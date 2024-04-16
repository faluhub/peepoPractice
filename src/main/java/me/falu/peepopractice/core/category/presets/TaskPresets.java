package me.falu.peepopractice.core.category.presets;

import me.falu.peepopractice.core.CustomPortalForcer;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.preferences.PreferenceTypes;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.exception.NotInitializedException;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.ServerWorldProperties;

public class TaskPresets {
    public static final PracticeCategory.ExecuteReturnTask<BlockPos> BASTION_SPAWN_POS = (category, random, world) -> {
        if (category.hasCustomValue("bastionType")) {
            PreferenceTypes.SpawnLocationType spawnLocation = CategoryPreferences.SPAWN_LOCATION.getValue();
            if (spawnLocation.equals(PreferenceTypes.SpawnLocationType.STRUCTURE)) {
                PreferenceTypes.BastionType type = PreferenceTypes.BastionType.fromId((int) category.getCustomValue("bastionType"));
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
            } else {
                ServerWorldProperties props = world.getServer().getSaveProperties().getMainWorldProperties();
                BlockPos pos = new BlockPos(props.getSpawnX(), props.getSpawnY(), props.getSpawnZ());
                pos = CustomPortalForcer.createPortal(pos, world).down(2);
                return pos;
            }
        }
        throw new NotInitializedException();
    };
    public static final PracticeCategory.ExecuteReturnTask<Vec2f> BASTION_SPAWN_ANGLE = (category, random, world) -> {
        if (category.hasCustomValue("bastionType")) {
            PreferenceTypes.SpawnLocationType spawnLocation = CategoryPreferences.SPAWN_LOCATION.getValue();
            if (spawnLocation.equals(PreferenceTypes.SpawnLocationType.STRUCTURE)) {
                PreferenceTypes.BastionType type = PreferenceTypes.BastionType.fromId((int) category.getCustomValue("bastionType"));
                if (type != null) {
                    return new Vec2f(type.angle, 0.0F);
                }
            } else {
                return new Vec2f(random.nextFloat(360.0F) - 180.0F, 0.0F);
            }
        }
        throw new NotInitializedException();
    };
}
