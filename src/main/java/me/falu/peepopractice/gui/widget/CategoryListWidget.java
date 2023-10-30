package me.falu.peepopractice.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.falu.peepopractice.PeepoPractice;
import me.falu.peepopractice.core.category.PracticeCategory;
import me.falu.peepopractice.core.category.properties.event.SplitEvent;
import me.falu.peepopractice.gui.screen.CategorySelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public abstract class CategoryListWidget extends AlwaysSelectedEntryListWidget<CategoryListWidget.CategoryEntry> {
    private final Screen screen;
    private final boolean hideDecorations;

    public CategoryListWidget(Screen screen, MinecraftClient client, boolean hideDecorations, boolean hideUnconfigured, CategorySelectionScreen.SelectionType type) {
        super(
                client,
                screen.width,
                screen.height,
                32,
                screen.height - 64 + 4,
                18
        );
        this.screen = screen;
        this.hideDecorations = hideDecorations;

        for (PracticeCategory category : type.list) {
            if (!category.isHidden()) {
                if (hideUnconfigured && !category.hasConfiguredInventory()) {
                    continue;
                }
                this.addEntry(new CategoryEntry(category));
            }
        }

        if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
        }
    }

    public abstract void onDoubleClick(PracticeCategory category);

    @Override
    protected int getScrollbarPositionX() {
        return this.width - 6;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    public int getTop() {
        return this.top;
    }

    public int getBottom() {
        return this.bottom;
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {
        PeepoPractice.drawBackground(matrices, this, this.screen);
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int i = this.getItemCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int j = 0; j < i; ++j) {
            int p;
            int k = this.getRowTop(j);
            int l = this.getRowBottom(j);
            if (l < this.top || k > this.bottom) continue;
            int m = y + j * this.itemHeight + this.headerHeight;
            int n = this.itemHeight - 4;
            EntryListWidget.Entry<?> entry = this.getEntry(j);
            int o = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(j)) {
                p = this.left + this.width / 2 - o / 2;
                int q = this.left + this.width / 2 + o / 2;
                RenderSystem.disableTexture();
                float f = this.isFocused() ? 1.0f : 0.5f;
                RenderSystem.color4f(f, f, f, 1.0f);
                bufferBuilder.begin(7, VertexFormats.POSITION);
                bufferBuilder.vertex(p, m + n + 2, 0.0).next();
                bufferBuilder.vertex(q, m + n + 2, 0.0).next();
                bufferBuilder.vertex(q, m - 2, 0.0).next();
                bufferBuilder.vertex(p, m - 2, 0.0).next();
                tessellator.draw();
                RenderSystem.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                this.fillGradient(matrices, p + 1, m - 1, q - 1, m + n + 1, PeepoPractice.BACKGROUND_COLOR[0], PeepoPractice.BACKGROUND_COLOR[1]);
                this.fillGradient(matrices, q - 1, m + n + 1, p + 1, m - 1, PeepoPractice.BACKGROUND_COLOR[0], PeepoPractice.BACKGROUND_COLOR[1]);
                RenderSystem.enableTexture();
            }
            p = this.getRowLeft();
            entry.render(matrices, j, k, p, o, n, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), entry), delta);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = this.getScrollbarPositionX();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.fillGradient(matrices, this.left, this.top, this.right, this.bottom, PeepoPractice.BACKGROUND_COLOR[0], PeepoPractice.BACKGROUND_COLOR[1]);
        this.fillGradient(matrices, this.left, this.top, this.right, this.bottom, PeepoPractice.BACKGROUND_OVERLAY_COLOR, PeepoPractice.BACKGROUND_OVERLAY_COLOR);
        int k = this.getRowLeft();
        int l = this.top + 4 - (int) this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(matrices, k, l, tessellator);
        }
        this.renderList(matrices, k, l, mouseX, mouseY, delta);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        int o = this.getMaxScroll();
        if (o > 0) {
            int p = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
            p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
            int q = (int) this.getScrollAmount() * (this.bottom - this.top - p) / o + this.top;
            if (q < this.top) {
                q = this.top;
            }
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(i, q + p, 0.0).texture(0.0f, 1.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, q + p, 0.0).texture(1.0f, 1.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, q, 0.0).texture(1.0f, 0.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i, q, 0.0).texture(0.0f, 0.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i, q + p - 1, 0.0).texture(0.0f, 1.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, q + p - 1, 0.0).texture(1.0f, 1.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, q, 0.0).texture(1.0f, 0.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(i, q, 0.0).texture(0.0f, 0.0f).color(192, 192, 192, 255).next();
            tessellator.draw();
        }
        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    public class CategoryEntry extends AlwaysSelectedEntryListWidget.Entry<CategoryListWidget.CategoryEntry> {
        public final PracticeCategory category;

        public CategoryEntry(PracticeCategory category) {
            this.category = category;
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean showStats = CategoryListWidget.this.getSelected() == this && this.category.hasSplitEvent();
            Text label = new LiteralText(this.category.getName(!showStats && !CategoryListWidget.this.hideDecorations));
            if (showStats && !CategoryListWidget.this.hideDecorations) {
                SplitEvent event = this.category.getSplitEvent();
                label = label.copy().append(
                        Formatting.GRAY + " (" +
                                Formatting.WHITE + event.getAttempts() +
                                Formatting.GRAY + "/" +
                                Formatting.GREEN + event.getCompletionCount() +
                                Formatting.GRAY + "/" +
                                Formatting.RED + event.getFailCount() +
                                Formatting.GRAY + ")"
                );
            }

            CategoryListWidget.this.client.textRenderer.drawWithShadow(
                    matrices,
                    label.getString(),
                    (float) (CategoryListWidget.this.screen.width / 2 - CategoryListWidget.this.client.textRenderer.getWidth(label.getString()) / 2),
                    y + 1,
                    16777215,
                    true
            );
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                this.onPressed();
                return true;
            } else {
                return false;
            }
        }

        public void onPressed() {
            if (CategoryListWidget.this.getSelected() == this) {
                CategoryListWidget.this.onDoubleClick(this.category);
            } else {
                CategoryListWidget.this.setSelected(this);
            }
        }
    }
}
