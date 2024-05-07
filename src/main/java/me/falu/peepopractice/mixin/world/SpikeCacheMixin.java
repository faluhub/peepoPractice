package me.falu.peepopractice.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.preferences.PreferenceTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Random;

@Mixin(targets = "net/minecraft/world/gen/feature/EndSpikeFeature$SpikeCache")
public abstract class SpikeCacheMixin {
    @WrapOperation(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;Ljava/util/Random;)V"))
    private void peepoPractice$modifyTowerOrder(List<Integer> towers, Random random, Operation<Void> original) {
        original.call(towers, random);
        PreferenceTypes.StartNodeType startNode = CategoryPreferences.START_NODE.getValue();
        startNode = startNode.equals(PreferenceTypes.StartNodeType.RANDOM) ? this.getDragonType(towers) : startNode;
        if (startNode != this.getDragonType(towers)) {
            int temp = towers.get(0);
            towers.set(0, towers.get(5));
            towers.set(5, temp);
        }
        PreferenceTypes.EndTowerHeightType towerHeight = CategoryPreferences.TOWER_HEIGHT.getValue();
        if (towerHeight.equals(PreferenceTypes.EndTowerHeightType.RANDOM)) {
            return;
        }
        towers.set(startNode.equals(PreferenceTypes.StartNodeType.FRONT) ? 9 : 4, (towerHeight.height - 76) / 3);
    }

    @Unique
    private PreferenceTypes.StartNodeType getDragonType(List<Integer> towers) {
        return towers.get(0) > towers.get(5) ? PreferenceTypes.StartNodeType.FRONT : PreferenceTypes.StartNodeType.BACK;
    }
}
