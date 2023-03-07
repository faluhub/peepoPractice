package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.core.category.CategoryPreference;
import me.quesia.peepopractice.core.category.PracticeTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(targets = "net.minecraft.world.gen.feature.EndSpikeFeature$SpikeCache")
public class SpikeCacheMixin {
    @Redirect(method = "load(Ljava/lang/Long;)Ljava/util/List;", at = @At(value="INVOKE", target="Ljava/util/Collections;shuffle(Ljava/util/List;Ljava/util/Random;)V"))
    private void modifyTowerOrder(List<Integer> towers, Random random){
        PracticeTypes.StartNodeType configValue = PracticeTypes.StartNodeType.fromLabel(CategoryPreference.getValue("start_node"));
        if (configValue != null) {
            Collections.shuffle(towers, random);
            PracticeTypes.StartNodeType dragonType = configValue.equals(PracticeTypes.StartNodeType.RANDOM) ? this.getDragonType(towers) : configValue;
            if (dragonType != this.getDragonType(towers)){
                int temp = towers.get(0);
                towers.set(0,towers.get(5));
                towers.set(5,temp);
            }
        }
    }

    private PracticeTypes.StartNodeType getDragonType(List<Integer> towers) {
        return towers.get(0) > towers.get(5) ? PracticeTypes.StartNodeType.FRONT : PracticeTypes.StartNodeType.BACK;
    }
}
