package me.quesia.peepopractice.mixin.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategoryUtils;
import me.quesia.peepopractice.mixin.access.MoreOptionsDialogAccessor;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 7))
    private AbstractButtonWidget customButtons(GameMenuScreen instance, AbstractButtonWidget abstractButtonWidget) {
        if (this.client == null) { return null; }

        if (PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            if (abstractButtonWidget instanceof ButtonWidget) {
                ButtonWidget buttonWidget = (ButtonWidget) abstractButtonWidget;
                return this.addButton(buttonWidget);
            }
        }

        int i = -16;
        ButtonWidget button = this.addButton(
                new ButtonWidget(
                        this.width / 2 - 102,
                        this.height / 4 + 120 + i,
                        98,
                        20,
                        new LiteralText("Save & Quit"),
                        b -> {
                            b.active = false;
                            PracticeCategoryUtils.quit(true);
                        }
                )
        );
        this.addButton(
                new ButtonWidget(
                        this.width / 2 + 4,
                        this.height / 4 + 120 + i,
                        98,
                        20,
                        new LiteralText("Replay Split"),
                        b -> {
                            b.active = false;
                            this.client.openScreen(new CreateWorldScreen(null));
                        }
                )
        );
        return button;
    }

    @Override
    protected <T extends AbstractButtonWidget> T addButton(T button) {
        if (button.getMessage().getString().equals("menu.quitWorld")) { return button; }
        return super.addButton(button);
    }
}
