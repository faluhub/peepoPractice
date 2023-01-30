package me.quesia.peepopractice.mixin.gui.screen;

import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @Shadow @Final public MoreOptionsDialog moreOptionsDialog;
    @Shadow public boolean hardcore;

    @Inject(method = "createLevel", at = @At("TAIL"))
    private void resetVariables(CallbackInfo ci) {
        Random random = new Random(this.moreOptionsDialog.getGeneratorOptions(this.hardcore).getSeed());
        for (PracticeCategory category : PracticeCategories.ALL) {
            for (StructureProperties properties : category.getStructureProperties()) {
                properties.reset(random);
            }
        }
    }
}
