package net.silvking432.silvkingsmod.screen.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DarkAnvilScreen extends ForgingScreen<DarkAnvilScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/anvil.png");
    private static final Identifier TEXT_FIELD_TEXTURE = Identifier.ofVanilla("container/anvil/text_field");
    private static final Identifier TEXT_FIELD_DISABLED_TEXTURE = Identifier.ofVanilla("container/anvil/text_field_disabled");
    private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");

    private TextFieldWidget nameField;

    public DarkAnvilScreen(DarkAnvilScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
        this.titleX = 60;
        this.titleY = 18;
    }

    @Override
    protected void setup() {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;

        this.nameField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, Text.translatable("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setDrawsBackground(false);
        this.nameField.setMaxLength(50);
        this.nameField.setChangedListener(this::onRenamed);
        this.nameField.setText("");
        this.addSelectableChild(this.nameField);

        this.nameField.setEditable(this.handler.getSlot(0).hasStack());
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameField);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.nameField.getText();
        this.init(client, width, height);
        this.nameField.setText(string);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            assert this.client != null;
            assert this.client.player != null;
            this.client.player.closeHandledScreen();
            return true;
        }

        return this.nameField.keyPressed(keyCode, scanCode, modifiers) ||
                this.nameField.isActive() ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onRenamed(String name) {
        if (!this.handler.getSlot(0).hasStack()) return;

        String string = name;
        ItemStack stack = this.handler.getSlot(0).getStack();

        if (!stack.contains(DataComponentTypes.CUSTOM_NAME) && name.equals(stack.getName().getString())) {
            string = "";
        }

        this.handler.setNewItemName(string);
        assert this.client != null;
        this.client.getNetworkHandler().sendPacket(new RenameItemC2SPacket(string));
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        int cost = this.handler.getLevelCost();
        if (cost > 0) {
            int color = 8453920; // Grün
            Text text = Text.translatable("container.repair.cost", cost);

            if (cost >= 40 && !this.client.player.getAbilities().creativeMode) {
                text = Text.translatable("container.repair.expensive");
                color = 16736352;
            }
            else if (!this.handler.getSlot(2).canTakeItems(this.client.player)) {
                color = 16736352;
            }

            int xPos = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
            context.fill(xPos - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
            context.drawTextWithShadow(this.textRenderer, text, xPos, 69, color);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context, delta, mouseX, mouseY);
        context.drawGuiTexture(this.handler.getSlot(0).hasStack() ? TEXT_FIELD_TEXTURE : TEXT_FIELD_DISABLED_TEXTURE, this.x + 59, this.y + 20, 110, 16);
    }

    @Override
    public void renderForeground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.nameField.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) &&
                !this.handler.getSlot(2).hasStack()) {
            context.drawGuiTexture(ERROR_TEXTURE, x + 99, y + 45, 28, 21);
        }
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        if (slotId == 0) {
            this.nameField.setText(stack.isEmpty() ? "" : stack.getName().getString());
            this.nameField.setEditable(!stack.isEmpty());
            if (!stack.isEmpty()) {
                this.setFocused(this.nameField);
            }
        }
    }
}