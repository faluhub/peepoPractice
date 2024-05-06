package me.falu.peepopractice.gui.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.preferences.CategoryPreferences;
import me.falu.peepopractice.core.category.preferences.PreferenceTypes;
import me.falu.peepopractice.core.category.utils.InventoryUtils;
import me.falu.peepopractice.core.playerless.PlayerlessInventory;
import me.falu.peepopractice.core.writer.PracticeWriter;
import me.falu.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class InventoryOptionsScreen extends Screen {
    private static final Identifier EGG_TEXTURE = new Identifier("textures/item/egg.png");
    private static final Identifier BREWING_STAND_TEXTURE = new Identifier("textures/item/brewing_stand.png");
    private static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");
    private final Screen parent;
    private final PracticeCategory category;
    private final List<NamePositionData> namePositions;
    private final List<HotbarPositionData> hotbarPositions;

    public InventoryOptionsScreen(Screen parent, PracticeCategory category) {
        super(new TranslatableText("peepopractice.title.inventory_options", category.getSimpleName()));
        this.parent = parent;
        this.category = category;
        this.namePositions = new ArrayList<>();
        this.hotbarPositions = new ArrayList<>();
    }

    @Override
    protected void init() {
        this.namePositions.clear();
        this.hotbarPositions.clear();

        int containerHeight = this.height - 40;
        int paddingX = 8;
        int paddingY = 20;
        int rowButtonSize = containerHeight / 3 - paddingY;
        int hotbarWidth = 182;
        int rowWidth = rowButtonSize * 2 + paddingX * 2 + hotbarWidth;

        JsonObject object = PracticeWriter.INVENTORY_WRITER.get();
        JsonArray profiles = object.getAsJsonArray(this.category.getId());

        for (int i = 0; i < 3; i++) {
            int x = (this.width - rowWidth) / 2;
            int y = this.height - containerHeight + paddingY * i + rowButtonSize * i;
            boolean isCorrectBound = profiles.size() >= i;
            boolean isRandomInventory = CategoryPreferences.RANDOM_INVENTORY.getBoolValue(this.category);
            boolean isSelected = i == InventoryUtils.getSelectedInventory(this.category) && !isRandomInventory;
            int finalI = i;
            ButtonWidget editButton = this.addButton(new LimitlessButtonWidget(x, y, rowButtonSize, rowButtonSize, new LiteralText("Edit"), b -> {
                if (this.client != null) {
                    this.client.openScreen(EditInventoryScreen.create(this, this.category, finalI));
                }
            }));
            editButton.active = isCorrectBound;
            x += rowButtonSize + paddingX;
            this.namePositions.add(new NamePositionData(x, y, new LiteralText("Inventory " + (i + 1)).formatted(isSelected ? Formatting.BOLD : Formatting.RESET), !isCorrectBound));
            this.hotbarPositions.add(new HotbarPositionData(x, y + rowButtonSize - 22, i, !isCorrectBound));
            x += hotbarWidth + paddingX;
            ButtonWidget selectButton = this.addButton(new LimitlessButtonWidget(x, y, rowButtonSize, rowButtonSize, new LiteralText(isSelected ? "Selected" : "Select"), b -> {
                CategoryPreferences.SELECTED_INVENTORY.setValue(this.category, PreferenceTypes.SelectedInventoryType.values()[finalI]);
                if (this.client != null) {
                    this.client.openScreen(new InventoryOptionsScreen(this.parent, this.category));
                }
            }));
            selectButton.active = !isSelected && isCorrectBound && !isRandomInventory;
        }
        int otherButtonWidth = (this.width - rowWidth) / 2 - paddingX * 2;
        this.addButton(
                new LimitlessButtonWidget(
                        null,
                        EditInventoryScreen.BARRIER_TEXTURE,
                        null,
                        (this.width - rowWidth) / 2 - otherButtonWidth - paddingX,
                        this.height - containerHeight,
                        otherButtonWidth,
                        rowButtonSize * 3 + paddingY * 2,
                        ScreenTexts.DONE,
                        b -> this.onClose()
                )
        );
        ButtonWidget scrambleButton = this.addButton(
                new LimitlessButtonWidget(
                        null,
                        EGG_TEXTURE,
                        null,
                        (this.width - rowWidth) / 2 + rowWidth + paddingX,
                        this.height - containerHeight,
                        otherButtonWidth,
                        (rowButtonSize * 3 + paddingY * 2) / 2,
                        CategoryPreferencesScreen.getFormattedText(this.category, CategoryPreferences.SCRAMBLE_INVENTORY),
                        b -> {
                            CategoryPreferences.SCRAMBLE_INVENTORY.advanceValue(this.category);
                            b.setMessage(CategoryPreferencesScreen.getFormattedText(this.category, CategoryPreferences.SCRAMBLE_INVENTORY));
                        }
                )
        );
        this.addButton(
                new LimitlessButtonWidget(
                        null,
                        BREWING_STAND_TEXTURE,
                        null,
                        scrambleButton.x,
                        scrambleButton.y + scrambleButton.getHeight(),
                        scrambleButton.getWidth(),
                        scrambleButton.getHeight(),
                        CategoryPreferencesScreen.getFormattedText(this.category, CategoryPreferences.RANDOM_INVENTORY),
                        b -> {
                            CategoryPreferences.RANDOM_INVENTORY.advanceValue(this.category);
                            b.setMessage(CategoryPreferencesScreen.getFormattedText(this.category, CategoryPreferences.RANDOM_INVENTORY));
                            if (this.client != null) {
                                this.client.openScreen(new InventoryOptionsScreen(this.parent, this.category));
                            }
                        }
                )
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PeepoPractice.drawBackground(matrices, this);
        super.render(matrices, mouseX, mouseY, delta);

        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);

        for (NamePositionData i : this.namePositions) {
            RenderSystem.pushMatrix();
            float scale = 2.0F;
            RenderSystem.scalef(scale, scale, 1.0F);
            int color = i.disabled ? BackgroundHelper.ColorMixer.getArgb(255 / 2, 255, 255, 255) : 0xFFFFFF;
            this.textRenderer.drawWithShadow(matrices, i.name, i.x / scale, i.y / scale, color);
            RenderSystem.popMatrix();
        }
        for (HotbarPositionData i : this.hotbarPositions) {
            PlayerlessInventory inventory = new PlayerlessInventory();
            InventoryUtils.putItems(inventory, this.category, i.profileIndex);
            RenderSystem.pushMatrix();
            if (i.disabled) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
            }
            this.renderHotbar(matrices, i.x, i.y, inventory);
            RenderSystem.popMatrix();
        }
    }

    protected void renderHotbar(MatrixStack matrices, int x, int y, PlayerlessInventory inventory) {
        if (this.client == null) {
            return;
        }
        this.client.getTextureManager().bindTexture(WIDGETS);
        int j = this.getZOffset();
        this.setZOffset(-90);
        this.drawTexture(matrices, x, y, 0, 0, 3, 3);
        this.drawTexture(matrices, x + 3, y, 3, 0, 179, 3);
        this.drawTexture(matrices, x + 179, y, 179, 0, 3, 3);
        this.drawTexture(matrices, x + 179, y + 3, 179, 3, 3, 19);
        this.drawTexture(matrices, x + 179, y + 19, 179, 19, 3, 3);
        this.drawTexture(matrices, x + 3, y + 19, 3, 19, 179, 3);
        this.drawTexture(matrices, x, y + 19, 0, 19, 3, 3);
        this.drawTexture(matrices, x, y + 3, 0, 3, 3, 19);
        for (int i = 0; i < 8; i++) {
            int u = 3 + 16 * (i + 1) + 4 * i;
            this.drawTexture(matrices, x + u, y + 3, u, 3, 4, 19);
        }
        this.setZOffset(j);
        for (int i = 0; i < 9; ++i) {
            int n = x + 1 + i * 20 + 2;
            int o = y + 6 - 3;
            ItemStack item = inventory.main.get(i);
            if (!item.isEmpty()) {
                this.itemRenderer.renderInGuiWithOverrides(item, n, o);
                this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, item, n, o);
            }
        }
    }

    @Override
    public void onClose() {
        PracticeWriter.PREFERENCES_WRITER.write();

        if (this.client != null) {
            this.client.openScreen(this.parent);
        }
    }

    @RequiredArgsConstructor
    private static class NamePositionData {
        public final int x;
        public final int y;
        public final Text name;
        public final boolean disabled;
    }

    @RequiredArgsConstructor
    private static class HotbarPositionData {
        public final int x;
        public final int y;
        public final int profileIndex;
        public final boolean disabled;
    }
}
