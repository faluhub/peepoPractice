package me.quesia.peepopractice.mixin.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.utils.PracticeCategoryUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow private Difficulty field_24289;
    @Shadow private Difficulty field_24290;
    @Shadow private boolean cheatsEnabled;
    @Shadow private boolean tweakedCheats;
    @Shadow protected abstract void createLevel();
    @Shadow private String levelName;
    @Shadow @Final public MoreOptionsDialog moreOptionsDialog;
    @Shadow public boolean hardcore;

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void peepoPractice$setDefaultDifficulty(CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            this.field_24289 = this.field_24290 = Difficulty.EASY;
            this.cheatsEnabled = this.tweakedCheats = true;
            this.levelName = PeepoPractice.CATEGORY.getName(false);
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void peepoPractice$startCreateLevel(CallbackInfo ci) {
        if (PeepoPractice.CATEGORY.hasWorldProperties()) {
            for (Map.Entry<Biome, Integer> entry : PeepoPractice.CATEGORY.getWorldProperties().getProBiomeRangeMap().entrySet()) {
                if (entry.getValue() == null) {
                    this.moreOptionsDialog.field_25049 = java.util.Optional.of(GeneratorType.SINGLE_BIOME_SURFACE);
                    this.moreOptionsDialog.setGeneratorOptions(GeneratorType.method_29079(this.moreOptionsDialog.getGeneratorOptions(this.hardcore), GeneratorType.SINGLE_BIOME_SURFACE, entry.getKey()));
                    break;
                }
            }
        }
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            this.createLevel();
        }
    }

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void peepoPractice$checkDisconnected(CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            if (PeepoPractice.CATEGORY.hasSplitEvent()) {
                PeepoPractice.CATEGORY.getSplitEvent().incrementAttempts();
            }
            if (this.client != null && this.client.isIntegratedServerRunning()) {
                if (PeepoPractice.HAS_FAST_RESET) {
                    GameMenuScreen screen = new GameMenuScreen(true);
                    screen.init(this.client, this.width, this.height);
                    for (Element element : screen.children()) {
                        if (element instanceof ButtonWidget) {
                            ButtonWidget button = (ButtonWidget) element;
                            if (button.getMessage().getString().equals("Save & Quit")) {
                                PeepoPractice.RESET_CATEGORY = false;
                                button.onPress();
                                return;
                            }
                        }
                    }
                }
                PracticeCategoryUtils.quit(false);
            }
        }
    }
}
