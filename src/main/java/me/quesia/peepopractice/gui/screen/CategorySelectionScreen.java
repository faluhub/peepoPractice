package me.quesia.peepopractice.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class CategorySelectionScreen extends Screen {
    private final Screen parent;
    protected PracticeCategory selected;
    private CategoryListWidget categoryListWidget;
    private ButtonWidget doneButton;
    private ButtonWidget configureButton;

    public CategorySelectionScreen(Screen parent) {
        super(new LiteralText("Select Practice Category"));

        this.parent = parent;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.selected != null) {
            this.categoryListWidget.setSelected(null);
            return false;
        }

        return super.shouldCloseOnEsc();
    }

    public void openConfig() {
        if (this.client != null && this.selected != null) {
            this.client.openScreen(new SettingsTypeSelectionScreen(this, this.selected));
        }
    }

    public void play() {
        if (this.client != null && this.selected != null) {
            PeepoPractice.CATEGORY = this.selected;
            this.client.openScreen(new CreateWorldScreen(this));
        }
    }

    @Override
    protected void init() {
        this.categoryListWidget = new CategoryListWidget(this.client);
        this.children.add(this.categoryListWidget);

        this.doneButton = this.addButton(
                new LimitlessButtonWidget(
                        null,
                        null,
                        null,
                        this.width / 2 - 155,
                        this.height - 50,
                        150,
                        40,
                        ScreenTexts.BACK,
                        b -> {
                            if (this.client != null) {
                                if (this.doneButton.getMessage().getString().equals(ScreenTexts.BACK.getString())) {
                                    this.client.openScreen(this.parent);
                                } else {
                                    this.play();
                                }
                            }
                        }
                )
        );
        this.configureButton = this.addButton(
                new LimitlessButtonWidget(
                        null,
                        null,
                        null,
                        this.width / 2 - 155 + 160,
                        this.height - 50,
                        150,
                        40,
                        new LiteralText("Configure"),
                        b -> openConfig()
                )
        );

        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);

        if (this.categoryListWidget != null) {
            this.categoryListWidget.render(matrices, mouseX, mouseY, delta);
        }

        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);

        this.configureButton.active = this.selected != null;
        this.doneButton.setMessage(this.selected != null ? new LiteralText("Play!") : ScreenTexts.BACK);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (this.selected != null) {
            this.selected = null;
        } else {
            if (this.client != null) {
                this.client.openScreen(null);
            }
        }

        super.onClose();
    }

    private class CategoryListWidget extends AlwaysSelectedEntryListWidget<CategoryListWidget.CategoryEntry> {
        public CategoryListWidget(MinecraftClient client) {
            super(
                    client,
                    CategorySelectionScreen.this.width,
                    CategorySelectionScreen.this.height,
                    32,
                    CategorySelectionScreen.this.height - 64 + 4,
                    18
            );

            for (PracticeCategory category : PracticeCategories.ALL) {
                if (!category.isHidden()) {
                    this.addEntry(new CategoryEntry(category));
                }
            }

            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        @Override
        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        public void setSelected(CategoryEntry categoryEntry) {
            super.setSelected(categoryEntry);
            if (categoryEntry != null) { CategorySelectionScreen.this.selected = categoryEntry.category; }
            else { CategorySelectionScreen.this.selected = null; }
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
        }

        @Override
        protected boolean isFocused() {
            return CategorySelectionScreen.this.getFocused() == this;
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
                    this.fillGradient(matrices, p + 1, m - 1, q - 1, m + n + 1, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
                    this.fillGradient(matrices, q - 1, m + n + 1, p + 1, m - 1, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
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
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.fillGradient(matrices, this.left, this.top, this.right, this.bottom, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
            this.fillGradient(matrices, this.left, this.top, this.right, this.bottom, PeepoPractice.BACKGROUND_OVERLAY_COLOUR, PeepoPractice.BACKGROUND_OVERLAY_COLOUR);
            int k = this.getRowLeft();
            int l = this.top + 4 - (int)this.getScrollAmount();
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
                int p = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
                p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
                int q = (int)this.getScrollAmount() * (this.bottom - this.top - p) / o + this.top;
                if (q < this.top) {
                    q = this.top;
                }
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(i, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
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
                Text label = new LiteralText(this.category.getName(true));

                CategorySelectionScreen.this.textRenderer.drawWithShadow(
                        matrices,
                        label.getString(),
                        (float) (CategorySelectionScreen.this.width / 2 - CategorySelectionScreen.this.textRenderer.getWidth(label.getString()) / 2),
                        y + 1,
                        16777215,
                        true
                );
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                } else { return false; }
            }

            public void onPressed() {
                if (CategoryListWidget.this.getSelected() == this) { CategorySelectionScreen.this.play(); }
                else { CategoryListWidget.this.setSelected(this); }
            }
        }
    }
}
