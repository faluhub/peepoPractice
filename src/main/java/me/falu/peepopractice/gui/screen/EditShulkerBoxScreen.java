package me.falu.peepopractice.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.playerless.PlayerlessHandledScreen;
import me.falu.peepopractice.core.playerless.PlayerlessInventory;
import me.falu.peepopractice.core.playerless.PlayerlessShulkerBoxScreenHandler;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class EditShulkerBoxScreen extends PlayerlessHandledScreen {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/shulker_box.png");
    private final EditInventoryScreen parent;
    private final Slot slot;
    private final PlayerlessShulkerBoxScreenHandler shulkerHandler;

    public EditShulkerBoxScreen(EditInventoryScreen parent, Slot slot, PlayerlessInventory inventory, Text title) {
        super(new PlayerlessShulkerBoxScreenHandler(inventory), inventory, title);
        ++this.backgroundHeight;
        this.parent = parent;
        this.slot = slot;
        this.shulkerHandler = (PlayerlessShulkerBoxScreenHandler) this.getScreenHandler();
        this.shulkerHandler.initializeItems(slot.getStack());
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new LimitlessButtonWidget(null, new Identifier("textures/item/barrier.png"), null, this.x - this.x / 2 - (this.width / 8) / 2, this.y, this.width / 8, this.backgroundHeight, ScreenTexts.DONE, b -> this.onClose()));
        ButtonWidget copyButton = this.addButton(new LimitlessButtonWidget(null, new Identifier("textures/item/chest_minecart.png"), null, this.x + this.backgroundWidth + this.x / 2 - (this.width / 8) / 2, this.y, this.width / 8, this.backgroundHeight, new TranslatableText("peepopractice.button.copy_from_existing"), b -> {}));
        copyButton.active = false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PeepoPractice.drawBackground(matrices, this);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        if (this.client == null) { return; }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void onClose() {
        if (this.client == null) { return; }
        ItemStack stack = this.slot.getStack();
        stack.getOrCreateSubTag("BlockEntityTag").put("Items", this.shulkerHandler.getItemsTag());
        this.slot.setStack(stack);
        this.slot.markDirty();
        this.client.openScreen(this.parent);
    }

    @Override
    protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        if (slot == null || slot.id - this.shulkerHandler.size() + 9 != this.slot.id) {
            super.onMouseClick(slot, invSlot, clickData, actionType);
        }
    }
}
