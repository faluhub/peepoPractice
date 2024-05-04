package me.falu.peepopractice.mixin.gui;

import me.falu.peepopractice.core.category.utils.InventoryUtils;
import net.minecraft.recipe.book.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBook.class)
public class RecipeBookMixin {
    @Inject(method = "setGuiOpen", at = @At("HEAD"))
    private void saveBookState(boolean guiOpen, CallbackInfo ci) {
        InventoryUtils.BOOK_OPEN = guiOpen;
    }

    @Inject(method = "setFilteringCraftable", at = @At("HEAD"))
    private void saveFilterState(boolean filteringCraftable, CallbackInfo ci) {
        InventoryUtils.FILTERING_CRAFTABLE = filteringCraftable;
    }
}
