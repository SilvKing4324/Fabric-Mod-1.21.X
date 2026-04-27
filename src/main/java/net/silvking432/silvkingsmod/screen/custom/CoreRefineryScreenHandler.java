package net.silvking432.silvkingsmod.screen.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.silvking432.silvkingsmod.block.entity.custom.CoreRefineryBlockEntity;
import net.silvking432.silvkingsmod.network.BlockPosPayload;
import net.silvking432.silvkingsmod.screen.ModScreenHandlers;

public class CoreRefineryScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final CoreRefineryBlockEntity blockEntity;

    public CoreRefineryScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (Inventory) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    public CoreRefineryScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.CORE_REFINERY_SCREEN_HANDLER, syncId);
        checkSize(inventory, 3);
        this.inventory = inventory;
        this.blockEntity = inventory instanceof CoreRefineryBlockEntity be ? be : null;

        this.addSlot(new Slot(inventory, 0, 44, 35));  // Armor
        this.addSlot(new Slot(inventory, 1, 80, 35));  // Weapon
        this.addSlot(new Slot(inventory, 2, 116, 35)); // Tool

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public void requestRefine() {
        if (this.blockEntity != null) {
            this.blockEntity.refineCores();
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // Standard QuickMove Logik hier einfügen (Shift-Klick)
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}