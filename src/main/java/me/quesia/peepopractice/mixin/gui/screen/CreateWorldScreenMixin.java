package me.quesia.peepopractice.mixin.gui.screen;

import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.properties.StructureProperties;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @Inject(method = "createLevel", at = @At("TAIL"))
    private void resetVariables(CallbackInfo ci) {
        for (PracticeCategory category : PracticeCategories.ALL) {
            if (!category.getStructureProperties().isEmpty()) {
                for (StructureProperties properties : category.getStructureProperties()) {
                    properties.hasGenerated = false;
                }
            }
        }
    }
}
