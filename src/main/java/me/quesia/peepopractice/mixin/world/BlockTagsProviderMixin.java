package me.quesia.peepopractice.mixin.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.data.server.BlockTagsProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockTagsProvider.class)
public abstract class BlockTagsProviderMixin extends AbstractTagProvider<Block> {
    protected BlockTagsProviderMixin(DataGenerator root, Registry<Block> registry) {
        super(root, registry);
    }

    @Inject(method = "configure", at = @At("TAIL"))
    private void addAlternativeBlocks(CallbackInfo ci) {
        this.getOrCreateTagBuilder(BlockTags.VALID_SPAWN).add(Blocks.NETHERRACK, Blocks.END_STONE);
    }
}
