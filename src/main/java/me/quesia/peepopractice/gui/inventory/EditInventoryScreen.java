package me.quesia.peepopractice.gui.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.InventoryUtils;
import me.quesia.peepopractice.core.PracticeCategory;
import me.quesia.peepopractice.core.PracticeWriter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class EditInventoryScreen extends PlayerlessHandledScreen {
    public static final Identifier TABS_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    public static final SimpleInventory displayInv = new SimpleInventory(45);
    private static int selectedTab;
    private float scrollPosition;
    private boolean scrolling;
    private TextFieldWidget searchBox;
    private ImmutableList<Slot> slots;
    @Nullable private Slot deleteItemSlot;
    private boolean ignoreTypedCharacter;
    private final Map<Identifier, Tag<Item>> searchResultTags = Maps.newTreeMap();
    private final Screen parent;
    private final PracticeCategory category;

    public EditInventoryScreen(Screen parent, PracticeCategory category) {
        super(new PlayerlessCreativeScreenHandler(), PeepoPractice.PLAYERLESS_INVENTORY, LiteralText.EMPTY);
        this.backgroundHeight = 136;
        this.backgroundWidth = 195;
        this.parent = parent;
        this.category = category;

        JsonObject config = PracticeWriter.INVENTORY_WRITER.get();
        if (config.has(this.category.id)) {
            JsonObject object = config.getAsJsonObject(this.category.id);
            object.entrySet().forEach(set -> {
                try {
                    CompoundTag tag = StringNbtReader.parse(set.getValue().getAsString());
                    ItemStack stack = ItemStack.fromTag(tag);
                    PeepoPractice.PLAYERLESS_INVENTORY.setStack(Integer.parseInt(set.getKey()), stack);
                } catch (CommandSyntaxException ignored) {
                    PeepoPractice.LOGGER.error("Couldn't parse inventory contents for inventory '{}'.", this.category.id);
                } catch (NumberFormatException ignored) {
                    PeepoPractice.LOGGER.error("Couldn't parse slot index: '{}' is not a valid number.", set.getKey());
                }
            });
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (selectedTab == InventoryUtils.LOOT_TABLES.getIndex()) {
            PlayerlessCreativeScreenHandler handler1 = (PlayerlessCreativeScreenHandler) this.handler;
            List<ItemStack> copy = new ArrayList<>(handler1.itemList);
            handler1.initLootTableItems();
            if (copy.size() != handler1.itemList.size()) {
                this.setSelectedTab(InventoryUtils.LOOT_TABLES);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onClose() {
        if (this.client == null) { return; }

        JsonObject object = new JsonObject();

        for (int i = 0; i < PeepoPractice.PLAYERLESS_INVENTORY.size(); i++) {
            ItemStack stack = PeepoPractice.PLAYERLESS_INVENTORY.getStack(i);
            if (stack != null && !stack.isEmpty()) {
                CompoundTag tag = new CompoundTag();
                object.addProperty(String.valueOf(i), stack.toTag(tag).toString());
            }
        }

        PeepoPractice.PLAYERLESS_INVENTORY.clear();

        PracticeWriter.INVENTORY_WRITER.put(this.category.id, object);

        this.client.openScreen(this.parent);
    }

    @Override
    public void tick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
        super.tick();
    }

    @Override
    protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        if (this.isCreativeInventorySlot(slot)) {
            this.searchBox.setCursorToEnd();
            this.searchBox.setSelectionEnd(0);
        }

        boolean bl = actionType == SlotActionType.QUICK_MOVE;
        actionType = invSlot == -999 && actionType == SlotActionType.PICKUP ? SlotActionType.THROW : actionType;
        ItemStack itemStack3;
        PlayerlessInventory playerInventory;
        if (slot == null && selectedTab != ItemGroup.INVENTORY.getIndex() && actionType != SlotActionType.QUICK_CRAFT) {
            playerInventory = PeepoPractice.PLAYERLESS_INVENTORY;
            if (!playerInventory.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
                if (clickData == 0) {
                    playerInventory.setCursorStack(ItemStack.EMPTY);
                }

                if (clickData == 1) {
                    playerInventory.getCursorStack().split(1);
                }
            }
        } else {
            if (!bl) {
                ItemStack itemStack8;
                if (
                        slot != null
                        && (
                                slot.inventory == this.playerInventory
                                || selectedTab == ItemGroup.INVENTORY.getIndex()
                        )
                        && actionType == SlotActionType.PICKUP
                        && slot.getStack() != null
                        && slot.getStack() != ItemStack.EMPTY
                        && PeepoPractice.PLAYERLESS_INVENTORY.getCursorStack() != null
                        && PeepoPractice.PLAYERLESS_INVENTORY.getCursorStack().getItem().equals(Items.ENCHANTED_BOOK)
                ) {
                    ListTag enchantments = EnchantedBookItem.getEnchantmentTag(PeepoPractice.PLAYERLESS_INVENTORY.getCursorStack());
                    AtomicBoolean shouldStop = new AtomicBoolean(false);
                    enchantments.forEach(rawTag -> {
                        if (shouldStop.get()) { return; }

                        ItemStack stack = slot.getStack();
                        CompoundTag tag = (CompoundTag) rawTag;
                        Enchantment enchantment = Registry.ENCHANTMENT.get(new Identifier(tag.getString("id")));
                        if (enchantment != null) {
                            if (!enchantment.isAcceptableItem(stack)) {
                                shouldStop.set(true);
                                return;
                            }

                            if (!stack.getEnchantments().isEmpty()) {
                                stack.getEnchantments().forEach(rawTag1 -> {
                                    CompoundTag tag1 = (CompoundTag) rawTag1;
                                    if (!enchantment.canCombine(Registry.ENCHANTMENT.get(new Identifier(tag1.getString("id"))))) {
                                        shouldStop.set(true);
                                    }
                                });
                            }

                            if (shouldStop.get()) { return; }

                            slot.getStack().addEnchantment(enchantment, tag.getInt("lvl"));
                        }
                    });

                    if (!shouldStop.get()) {
                        PeepoPractice.PLAYERLESS_INVENTORY.setCursorStack(ItemStack.EMPTY);
                        return;
                    }
                }

                if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
                    if (slot == this.deleteItemSlot) {
                        PeepoPractice.PLAYERLESS_INVENTORY.setCursorStack(ItemStack.EMPTY);
                    } else if (actionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
                        slot.takeStack(clickData == 0 ? 1 : slot.getStack().getMaxCount());
                    } else if (actionType == SlotActionType.THROW && !PeepoPractice.PLAYERLESS_INVENTORY.getCursorStack().isEmpty()) {
                        PeepoPractice.PLAYERLESS_INVENTORY.setCursorStack(ItemStack.EMPTY);
                    } else if (this.handler != null) {
                        this.handler.onSlotClick(slot == null ? invSlot : slot.id, clickData, actionType, PeepoPractice.PLAYERLESS_INVENTORY);
                    }
                } else if (
                        selectedTab == InventoryUtils.LOOT_TABLES.getIndex()
                        && actionType == SlotActionType.PICKUP
                        && slot.getStack() != null
                        && slot.getStack().getTag() != null
                        && slot.getStack().getTag().contains("LootTableId")
                ) {
                    ItemStack stack = slot.getStack();
                    this.scrollPosition = 0.0F;
                    Identifier identifier = new Identifier(stack.getTag().getString("LootTableId"));
                    List<ItemStack> items = InventoryUtils.getLootTableItems(identifier);
                    ((PlayerlessCreativeScreenHandler) this.handler).itemList.clear();

                    displayInv.clear();
                    for (ItemStack stack1 : items) {
                        displayInv.addStack(stack1);
                        ((PlayerlessCreativeScreenHandler) this.handler).itemList.add(items.indexOf(stack1), stack1);
                    }
                } else {
                    ItemStack itemStack10;
                    if (actionType != SlotActionType.QUICK_CRAFT && slot.inventory == displayInv) {
                        playerInventory = PeepoPractice.PLAYERLESS_INVENTORY;
                        itemStack3 = playerInventory.getCursorStack();
                        ItemStack itemStack4 = slot.getStack();
                        if (actionType == SlotActionType.SWAP) {
                            if (!itemStack4.isEmpty()) {
                                itemStack10 = itemStack4.copy();
                                itemStack10.setCount(itemStack10.getMaxCount());
                                PeepoPractice.PLAYERLESS_INVENTORY.setStack(clickData, itemStack10);
                            }

                            return;
                        }

                        if (actionType == SlotActionType.CLONE) {
                            if (playerInventory.getCursorStack().isEmpty() && slot.hasStack()) {
                                itemStack10 = slot.getStack().copy();
                                itemStack10.setCount(itemStack10.getMaxCount());
                                playerInventory.setCursorStack(itemStack10);
                            }

                            return;
                        }

                        if (actionType == SlotActionType.THROW) {
                            if (!itemStack4.isEmpty()) {
                                itemStack10 = itemStack4.copy();
                                itemStack10.setCount(clickData == 0 ? 1 : itemStack10.getMaxCount());
                            }

                            return;
                        }

                        if (!itemStack3.isEmpty() && !itemStack4.isEmpty() && itemStack3.isItemEqualIgnoreDamage(itemStack4) && ItemStack.areTagsEqual(itemStack3, itemStack4)) {
                            if (clickData == 0) {
                                if (itemStack3.getCount() < itemStack3.getMaxCount()) {
                                    itemStack3.increment(1);
                                }
                            } else {
                                itemStack3.decrement(1);
                            }
                        } else if (!itemStack4.isEmpty() && itemStack3.isEmpty()) {
                            playerInventory.setCursorStack(itemStack4.copy());
                        } else if (clickData == 0) {
                            playerInventory.setCursorStack(ItemStack.EMPTY);
                        } else {
                            playerInventory.getCursorStack().decrement(1);
                        }
                    } else if (this.handler != null) {
                        itemStack8 = slot == null ? ItemStack.EMPTY : this.handler.getSlot(slot.id).getStack();
                        this.handler.onSlotClick(slot == null ? invSlot : slot.id, clickData, actionType, PeepoPractice.PLAYERLESS_INVENTORY);
                        if (ScreenHandler.unpackQuickCraftStage(clickData) != 2 && slot != null) {
                            if (actionType == SlotActionType.THROW && !itemStack8.isEmpty()) {
                                itemStack10 = itemStack8.copy();
                                itemStack10.setCount(clickData == 0 ? 1 : itemStack10.getMaxCount());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isCreativeInventorySlot(@Nullable Slot slot) {
        return slot != null && slot.inventory == displayInv;
    }

    @Override
    protected void init() {
        if (this.client == null) { return; }

        synchronized (PeepoPractice.SERVER_RESOURCE_MANAGER) {
            if (PeepoPractice.SERVER_RESOURCE_MANAGER.get() == null) {
                this.client.openScreen(new FatalErrorScreen(new LiteralText("Still reloading!"), new LiteralText("PeepoPractice is still reloading the local resource manager.")));
                return;
            }
        }

        super.init();
        this.client.keyboard.enableRepeatEvents(true);
        int vx = this.x + 82;
        int vy = this.y + 6;
        this.searchBox = new TextFieldWidget(this.textRenderer, vx, vy, 80, 9, new TranslatableText("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setHasBorder(false);
        this.searchBox.setVisible(false);
        this.searchBox.setEditableColor(16777215);
        this.children.add(this.searchBox);
        int i = selectedTab;
        selectedTab = -1;
        this.setSelectedTab(ItemGroup.GROUPS[i]);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.searchBox.getText();
        this.init(client, width, height);
        this.searchBox.setText(string);
        if (!this.searchBox.getText().isEmpty()) {
            this.search();
        }
    }

    @Override
    public void removed() {
        super.removed();

        if (this.client != null) {
            this.client.keyboard.enableRepeatEvents(false);
        }
    }

    public boolean charTyped(char chr, int keyCode) {
        if (this.ignoreTypedCharacter) {
            return false;
        } else if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            return false;
        } else {
            String string = this.searchBox.getText();
            if (this.searchBox.charTyped(chr, keyCode)) {
                if (!Objects.equals(string, this.searchBox.getText())) {
                    this.search();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            if (this.client != null && this.client.options.keyChat.matchesKey(keyCode, scanCode)) {
                this.ignoreTypedCharacter = true;
                this.setSelectedTab(ItemGroup.SEARCH);
                return true;
            } else {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        } else {
            boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot.hasStack();
            boolean bl2 = InputUtil.fromKeyCode(keyCode, scanCode).method_30103().isPresent();
            if (bl && bl2 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
                this.ignoreTypedCharacter = true;
                return true;
            } else {
                String string = this.searchBox.getText();
                if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
                    if (!Objects.equals(string, this.searchBox.getText())) {
                        this.search();
                    }

                    return true;
                } else {
                    return this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256 || super.keyPressed(keyCode, scanCode, modifiers);
                }
            }
        }
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void setSelectedTab(ItemGroup group) {
        int i = selectedTab;
        selectedTab = group.getIndex();
        this.cursorDragSlots.clear();
        ((PlayerlessCreativeScreenHandler)this.handler).itemList.clear();
        if (group == InventoryUtils.LOOT_TABLES || group != ItemGroup.SEARCH) {
            if (group == InventoryUtils.LOOT_TABLES) {
                ((PlayerlessCreativeScreenHandler) this.handler).initLootTableItems();
            }
            group.appendStacks(((PlayerlessCreativeScreenHandler)this.handler).itemList);
        }

        if (group == ItemGroup.INVENTORY) {
            PlayerlessPlayerScreenHandler screenHandler = PeepoPractice.PLAYERLESS_PLAYER_SCREEN_HANDLER;
            if (this.slots == null) {
                this.slots = ImmutableList.copyOf(this.handler.slots);
            }

            this.handler.slots.clear();

            int aa;

            for (int l = 0; l < screenHandler.slots.size(); ++l) {
                int t;
                int v;
                int w;
                int x;
                if (l >= 5 && l < 9) {
                    v = l - 5;
                    w = v / 2;
                    x = v % 2;
                    t = 54 + w * 54;
                    aa = 6 + x * 27;
                } else if (l < 5) {
                    t = -2000;
                    aa = -2000;
                } else if (l == 45) {
                    t = 35;
                    aa = 20;
                } else {
                    v = l - 9;
                    w = v % 9;
                    x = v / 9;
                    t = 9 + w * 18;
                    if (l >= 36) {
                        aa = 112;
                    } else {
                        aa = 54 + x * 18;
                    }
                }

                Slot slot = new EditInventoryScreen.CreativeSlot(screenHandler.slots.get(l), l, t, aa);
                this.handler.slots.add(slot);
            }

            this.deleteItemSlot = new Slot(displayInv, 0, 173, 112);
            this.handler.slots.add(this.deleteItemSlot);
        } else if (i == ItemGroup.INVENTORY.getIndex() && this.slots != null) {
            this.handler.slots.clear();
            this.handler.slots.addAll(this.slots);
            this.slots = null;
        }

        if (this.searchBox != null) {
            if (group == ItemGroup.SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.setFocusUnlocked(false);
                this.searchBox.setSelected(true);
                if (i != group.getIndex()) {
                    this.searchBox.setText("");
                }

                this.search();
            } else {
                this.searchBox.setVisible(false);
                this.searchBox.setFocusUnlocked(true);
                this.searchBox.setSelected(false);
                this.searchBox.setText("");
            }
        }

        this.scrollPosition = 0.0F;
        ((PlayerlessCreativeScreenHandler)this.handler).scrollItems(0.0F);
    }

    private void search() {
        ((PlayerlessCreativeScreenHandler)this.handler).itemList.clear();
        this.searchResultTags.clear();
        String string = this.searchBox.getText();
        if (string.isEmpty()) {
            for (Item item : Registry.ITEM) {
                item.appendStacks(ItemGroup.SEARCH, ((PlayerlessCreativeScreenHandler) this.handler).itemList);
            }
        } else if (this.client != null) {
            SearchableContainer<ItemStack> searchable2;
            if (string.startsWith("#")) {
                string = string.substring(1);
                searchable2 = this.client.getSearchableContainer(SearchManager.ITEM_TAG);
                this.searchForTags(string);
            } else {
                searchable2 = this.client.getSearchableContainer(SearchManager.ITEM_TOOLTIP);
            }

            ((PlayerlessCreativeScreenHandler)this.handler).itemList.addAll(searchable2.findAll(string.toLowerCase(Locale.ROOT)));
        }

        this.scrollPosition = 0.0F;
        ((PlayerlessCreativeScreenHandler)this.handler).scrollItems(0.0F);
    }

    private void searchForTags(String string) {
        int i = string.indexOf(58);
        Predicate<Identifier> predicate2;
        if (i == -1) {
            predicate2 = (identifier) -> identifier.getPath().contains(string);
        } else {
            String string2 = string.substring(0, i).trim();
            String string3 = string.substring(i + 1).trim();
            predicate2 = (identifier) -> identifier.getNamespace().contains(string2) && identifier.getPath().contains(string3);
        }

        TagContainer<Item> tagContainer = ItemTags.getContainer();
        tagContainer.getKeys().stream().filter(predicate2).forEach((identifier) -> this.searchResultTags.put(identifier, tagContainer.get(identifier)));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            ItemGroup[] var10 = ItemGroup.GROUPS;

            for (ItemGroup itemGroup : var10) {
                if (this.isClickInTab(itemGroup, d, e)) {
                    return true;
                }
            }

            if (selectedTab != ItemGroup.INVENTORY.getIndex() && this.isClickInScrollbar(mouseX, mouseY)) {
                this.scrolling = this.hasScrollbar();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        int i = this.x;
        int j = this.y;
        int k = i + 175;
        int l = j + 18;
        int m = k + 14;
        int n = l + 112;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            this.scrolling = false;
            ItemGroup[] var10 = ItemGroup.GROUPS;

            for (ItemGroup itemGroup : var10) {
                if (this.isClickInTab(itemGroup, d, e)) {
                    this.setSelectedTab(itemGroup);
                    return true;
                }
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected boolean isClickInTab(ItemGroup group, double mouseX, double mouseY) {
        int i = group.getColumn();
        int j = 28 * i;
        if (group.isSpecial()) {
            j = this.backgroundWidth - 28 * (6 - i) + 2;
        } else if (i > 0) {
            j += i;
        }

        int k = 0;
        if (group.isTopRow()) {
            k = k - 32;
        } else {
            k = k + this.backgroundHeight;
        }

        return mouseX >= (double)j && mouseX <= (double)(j + 28) && mouseY >= (double)k && mouseY <= (double)(k + 32);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        if (this.client == null) { return; }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
        ItemGroup[] var6 = ItemGroup.GROUPS;
        int j = var6.length;

        int k;
        for (k = 0; k < j; ++k) {
            ItemGroup itemGroup2 = var6[k];
            this.client.getTextureManager().bindTexture(TABS_TEXTURE);
            if (itemGroup2.getIndex() != selectedTab) {
                this.renderTabIcon(matrices, itemGroup2);
            }
        }

        this.client.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.x + 175;
        j = this.y + 18;
        k = j + 112;
        this.client.getTextureManager().bindTexture(TABS_TEXTURE);
        if (itemGroup.hasScrollbar()) {
            this.drawTexture(matrices, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
        }

        this.renderTabIcon(matrices, itemGroup);
    }

    private boolean hasScrollbar() {
        return selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTab].hasScrollbar() && ((PlayerlessCreativeScreenHandler) this.handler).shouldShowScrollbar();
    }

    @SuppressWarnings("deprecation")
    protected void renderTabIcon(MatrixStack matrixStack, ItemGroup itemGroup) {
        boolean bl = itemGroup.getIndex() == selectedTab;
        boolean bl2 = itemGroup.isTopRow();
        int i = itemGroup.getColumn();
        int j = i * 28;
        int k = 0;
        int l = this.x + 28 * i;
        int m = this.y;
        if (bl) {
            k += 32;
        }

        if (itemGroup.isSpecial()) {
            l = this.x + this.backgroundWidth - 28 * (6 - i);
        } else if (i > 0) {
            l += i;
        }

        if (bl2) {
            m -= 28;
        } else {
            k += 64;
            m += this.backgroundHeight - 4;
        }

        this.drawTexture(matrixStack, l, m, j, k, 28, 32);
        this.itemRenderer.zOffset = 100.0F;
        l += 6;
        m += 8 + (bl2 ? 1 : -1);
        RenderSystem.enableRescaleNormal();
        ItemStack itemStack = itemGroup.getIcon();
        this.itemRenderer.renderInGuiWithOverrides(itemStack, l, m);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, m);
        this.itemRenderer.zOffset = 0.0F;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.hasScrollbar()) {
            return false;
        } else {
            int i = (((PlayerlessCreativeScreenHandler)this.handler).itemList.size() + 9 - 1) / 9 - 5;
            this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
            ((PlayerlessCreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
            return true;
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int i = this.y + 18;
            int j = i + 112;
            this.scrollPosition = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
            ((PlayerlessCreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        ItemGroup[] var5 = ItemGroup.GROUPS;

        for (ItemGroup itemGroup : var5) {
            if (this.renderTabTooltipIfHovered(matrices, itemGroup, mouseX, mouseY)) {
                break;
            }
        }

        if (this.deleteItemSlot != null && selectedTab == ItemGroup.INVENTORY.getIndex() && this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, mouseX, mouseY)) {
            this.renderTooltip(matrices, new TranslatableText("inventory.binSlot"), mouseX, mouseY);
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        if (selectedTab == ItemGroup.SEARCH.getIndex() && this.client != null) {
            List<Text> list = stack.getTooltip(null, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            List<Text> list2 = Lists.newArrayList(list);
            Item item = stack.getItem();
            ItemGroup itemGroup = item.getGroup();
            if (itemGroup == null && item == Items.ENCHANTED_BOOK) {
                Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
                if (map.size() == 1) {
                    Enchantment enchantment = map.keySet().iterator().next();
                    ItemGroup[] var11 = ItemGroup.GROUPS;

                    for (ItemGroup itemGroup2 : var11) {
                        if (itemGroup2.containsEnchantments(enchantment.type)) {
                            itemGroup = itemGroup2;
                            break;
                        }
                    }
                }
            }

            this.searchResultTags.forEach((identifier, tag) -> {
                if (tag.contains(item)) {
                    list2.add(1, (new LiteralText("#" + identifier)).formatted(Formatting.DARK_PURPLE));
                }

            });
            if (itemGroup != null) {
                list2.add(1, (new TranslatableText(itemGroup.getTranslationKey())).formatted(Formatting.BLUE));
            }

            this.renderTooltip(matrices, list2, x, y);
        } else {
            super.renderTooltip(matrices, stack, x, y);
        }

    }

    protected boolean renderTabTooltipIfHovered(MatrixStack matrixStack, ItemGroup itemGroup, int i, int j) {
        int k = itemGroup.getColumn();
        int l = 28 * k;
        if (itemGroup.isSpecial()) {
            l = this.backgroundWidth - 28 * (6 - k) + 2;
        } else if (k > 0) {
            l += k;
        }

        int m = 0;
        if (itemGroup.isTopRow()) {
            m = m - 32;
        } else {
            m = m + this.backgroundHeight;
        }

        if (this.isPointWithinBounds(l + 3, m + 3, 23, 27, i, j)) {
            this.renderTooltip(matrixStack, new TranslatableText(itemGroup.getTranslationKey()), i, j);
            return true;
        } else {
            return false;
        }
    }

    static {
        selectedTab = ItemGroup.BUILDING_BLOCKS.getIndex();
    }

    @Environment(EnvType.CLIENT)
    static class LockedSlot extends Slot {
        public LockedSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        @Override
        public boolean canTakeItems(PlayerEntity Entity) {
            return false;
        }
    }

    @Environment(EnvType.CLIENT)
    static class LockableSlot extends Slot {
        public LockableSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        public boolean canTakeItems(PlayerEntity playerEntity) {
            if (super.canTakeItems(playerEntity) && this.hasStack()) {
                return this.getStack().getSubTag("CustomCreativeLock") == null;
            } else {
                return !this.hasStack();
            }
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    @Environment(EnvType.CLIENT)
    static class CreativeSlot extends Slot {
        private final Slot slot;

        public CreativeSlot(Slot slot, int invSlot, int x, int y) {
            super(slot.inventory, invSlot, x, y);
            this.id = invSlot;
            this.slot = slot;
        }

        public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
            return this.slot.onTakeItem(player, stack);
        }

        public boolean canInsert(ItemStack stack) {
            return this.slot.canInsert(stack);
        }

        public ItemStack getStack() {
            return this.slot.getStack();
        }

        public boolean hasStack() {
            return this.slot.hasStack();
        }

        public void setStack(ItemStack stack) {
            this.slot.setStack(stack);
        }

        public void markDirty() {
            this.slot.markDirty();
        }

        public int getMaxStackAmount() {
            return this.slot.getMaxStackAmount();
        }

        public int getMaxStackAmount(ItemStack stack) {
            return this.slot.getMaxStackAmount(stack);
        }

        @Nullable
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }

        public ItemStack takeStack(int amount) {
            return this.slot.takeStack(amount);
        }

        public boolean doDrawHoveringEffect() {
            return this.slot.doDrawHoveringEffect();
        }

        public boolean canTakeItems(PlayerEntity playerEntity) {
            return this.slot.canTakeItems(playerEntity);
        }
    }

    public static class PlayerlessCreativeScreenHandler extends PlayerlessScreenHandler {
        public final DefaultedList<ItemStack> itemList = DefaultedList.of();

        public PlayerlessCreativeScreenHandler() {
            super();

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new EditInventoryScreen.LockableSlot(EditInventoryScreen.displayInv, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }

            for (int i = 0; i < 9; i++) {
                this.addSlot(new Slot(PeepoPractice.PLAYERLESS_INVENTORY, i, 9 + i * 18, 112));
            }

            this.scrollItems(0.0F);
        }

        public void initLootTableItems() {
            this.itemList.clear();

            int index = 0;

            List<String> tables = new ArrayList<>();
            LootTables.getAll().forEach(identifier -> tables.add(identifier.toString()));
            Collections.sort(tables);

            Random random = new Random(3);

            for (String key : tables) {
                if (key.contains("chests/")) {
                    StringBuilder name = new StringBuilder();
                    boolean shouldCapitalise = true;

                    String[] parts = key.split("/");
                    String part = parts[parts.length - 1];

                    for (Character c : part.toCharArray()) {
                        if (shouldCapitalise) {
                            name.append(c.toString().toUpperCase(Locale.ROOT));
                            shouldCapitalise = false;
                        } else if (c.equals('_')) {
                            name.append(" ");
                            shouldCapitalise = true;
                        } else {
                            name.append(c.toString().toLowerCase(Locale.ROOT));
                        }
                    }

                    List<ItemStack> items = InventoryUtils.getLootTableItems(new Identifier(key));

                    if (items.size() > 0) {
                        ItemStack item = new ItemStack(Items.CHEST);

                        CompoundTag tag = new CompoundTag();
                        tag.putString("DisplayItem", Registry.ITEM.getId(items.get(items.size() > 1 ? random.nextInt(items.size() - 1) : 0).getItem()).toString());
                        tag.putString("LootTableId", key);
                        tag.putBoolean("IgnoreItalic", true);
                        item.setTag(tag);
                        item.setCustomName(new LiteralText(name.toString()));

                        this.itemList.add(index, item);

                        index++;
                    }
                }
            }
        }

        public void scrollItems(float position) {
            int i = (this.itemList.size() + 9 - 1) / 9 - 5;
            int j = (int)((double)(position * (float)i) + 0.5D);
            if (j < 0) {
                j = 0;
            }

            for(int k = 0; k < 5; ++k) {
                for(int l = 0; l < 9; ++l) {
                    int m = l + (k + j) * 9;
                    if (m >= 0 && m < this.itemList.size()) {
                        EditInventoryScreen.displayInv.setStack(l + k * 9, this.itemList.get(m));
                    } else {
                        EditInventoryScreen.displayInv.setStack(l + k * 9, ItemStack.EMPTY);
                    }
                }
            }
        }

        public boolean shouldShowScrollbar() {
            return this.itemList.size() > 45;
        }

        public ItemStack transferSlot(int index) {
            if (index >= this.slots.size() - 9 && index < this.slots.size()) {
                Slot slot = this.slots.get(index);
                if (slot != null && slot.hasStack()) {
                    slot.setStack(ItemStack.EMPTY);
                }
            }

            return ItemStack.EMPTY;
        }
    }
}
