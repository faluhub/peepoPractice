package me.falu.peepopractice.core.category.properties.preset;

import me.falu.peepopractice.core.CustomPortalForcer;
import me.falu.peepopractice.core.category.CategoryPreference;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.PracticeTypes;
import me.falu.peepopractice.core.category.properties.StructureProperties;
import me.falu.peepopractice.core.category.utils.PracticeCategoryUtils;
import me.falu.peepopractice.core.exception.NotInitializedException;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.ServerWorldProperties;

public class BastionPreset {
    public static final PracticeCategory.ExecuteReturnTask<BlockPos> BASTION_SPAWN_POS = (category, random, world) -> {
        if (category.hasCustomValue("bastionType")) {
            PracticeTypes.SpawnLocationType spawnLocation = PracticeTypes.SpawnLocationType.fromLabel(CategoryPreference.getValue(category, "spawn_location"));
            if (spawnLocation == null || spawnLocation.equals(PracticeTypes.SpawnLocationType.STRUCTURE)) {
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
            PracticeTypes.SpawnLocationType spawnLocation = PracticeTypes.SpawnLocationType.fromLabel(CategoryPreference.getValue(category, "spawn_location"));
            if (spawnLocation == null || spawnLocation.equals(PracticeTypes.SpawnLocationType.STRUCTURE)) {
                PracticeTypes.BastionType type = PracticeTypes.BastionType.fromId((int) category.getCustomValue("bastionType"));
                if (type != null) {
                    return new Vec2f(type.angle, 0.0F);
                }
            } else {
                return new Vec2f(random.nextFloat(360.0F) - 180.0F, 0.0F);
            }
        }
        throw new NotInitializedException();
    };
    public static final CategoryPreference BASTION_TYPE_PREFERENCE = new CategoryPreference()
            .setId("bastion_type")
            .setIcon(new Identifier("textures/item/golden_helmet.png"))
            .setChoices(PracticeTypes.BastionType.all())
            .setDefaultChoice(PracticeTypes.BastionType.RANDOM.getLabel());
    public static final CategoryPreference RANKED_LOOT_TABLE_PREFERENCE = new CategoryPreference()
            .setId("ranked_loot_table")
            .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
            .setDefaultChoice(PracticeCategoryUtils.ENABLED)
            .setIcon(new Identifier("textures/item/ender_pearl.png"));
    public static final CategoryPreference ZOMBIE_PIGMEN = new CategoryPreference()
            .setId("zombie_pigmen")
            .setChoices(PracticeCategoryUtils.BOOLEAN_LIST)
            .setDefaultChoice(PracticeCategoryUtils.ENABLED)
            .setIcon(new Identifier("textures/item/rotten_flesh.png"));
}
