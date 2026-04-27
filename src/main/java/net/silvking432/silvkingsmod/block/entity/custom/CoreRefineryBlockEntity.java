package net.silvking432.silvkingsmod.block.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.silvking432.silvkingsmod.block.entity.ImplementedInventory;
import net.silvking432.silvkingsmod.block.entity.ModBlockEntities;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.screen.custom.CoreRefineryScreenHandler;
import net.silvking432.silvkingsmod.util.TraitGenerator;
import org.jetbrains.annotations.Nullable;

public class CoreRefineryBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public CoreRefineryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CORE_REFINERY_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Core Refinery");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CoreRefineryScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
    }

    public void refineCores() {
        ItemStack input1 = this.getStack(0);
        ItemStack input2 = this.getStack(1);
        ItemStack input3 = this.getStack(2);

        if (!input1.isEmpty()) {
            int tier = tierChecks(input1);
            if (tier > 0) {
                ItemStack result = new ItemStack(ModItems.REFINED_ARMOR_CORE);
                TraitGenerator.applyRandomTraits(result, tier, TraitGenerator.TraitApplication.ARMOR);
                this.setStack(0, result);
                this.markDirty();
            }
        }

        if (!input2.isEmpty()) {
            int tier = tierChecks(input2);
            if (tier > 0) {
                ItemStack result = new ItemStack(ModItems.REFINED_WEAPON_CORE);
                TraitGenerator.applyRandomTraits(result, tier, TraitGenerator.TraitApplication.WEAPON);
                this.setStack(1, result);
                this.markDirty();
            }
        }

        if (!input3.isEmpty()) {
            int tier = tierChecks(input3);
            if (tier > 0) {
                ItemStack result = new ItemStack(ModItems.REFINED_TOOL_CORE);
                TraitGenerator.applyRandomTraits(result, tier, TraitGenerator.TraitApplication.TOOL);
                this.setStack(2, result);
                this.markDirty();
            }
        }
    }

    private int tierChecks(ItemStack input) {
        int tier = 0;
        if (input.isOf(ModItems.DARK_CORE_TIER1)) tier = 1;
        else if (input.isOf(ModItems.DARK_CORE_TIER2)) tier = 2;
        else if (input.isOf(ModItems.DARK_CORE_TIER3)) tier = 3;
        else if (input.isOf(ModItems.DARK_CORE_TIER4)) tier = 4;
        else if (input.isOf(ModItems.DARK_CORE_TIER5)) tier = 5;
        return tier;
    }
}