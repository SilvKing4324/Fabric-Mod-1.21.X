package net.silvking432.silvkingsmod.block.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class EternalShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);

    private float animationProgress;
    private float prevAnimationProgress;
    private int viewerCount;

    public EternalShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ETERNAL_SHULKER_BOX_BE, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.silvkingsmod.eternal_shulker_box");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(net.minecraft.screen.ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, this, 6) {
            @Override
            public boolean canUse(PlayerEntity player) {
                return EternalShulkerBoxBlockEntity.this.canPlayerUse(player);
            }

            @Override
            protected net.minecraft.screen.slot.Slot addSlot(net.minecraft.screen.slot.Slot slot) {
                if (slot.inventory == EternalShulkerBoxBlockEntity.this) {
                    return super.addSlot(new net.minecraft.screen.slot.Slot(slot.inventory, slot.getIndex(), slot.x, slot.y) {
                        @Override
                        public boolean canInsert(ItemStack stack) {
                            return EternalShulkerBoxBlockEntity.this.canInsert(this.getIndex(), stack, null);
                        }
                    });
                }
                return super.addSlot(slot);
            }
        };
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }
    }

    public static void tick(net.minecraft.world.World world, BlockPos pos, BlockState state, EternalShulkerBoxBlockEntity blockEntity) {
        blockEntity.prevAnimationProgress = blockEntity.animationProgress;
        if (blockEntity.viewerCount > 0) {
            blockEntity.animationProgress = Math.min(1.0F, blockEntity.animationProgress + 0.1F);
        } else {
            blockEntity.animationProgress = Math.max(0.0F, blockEntity.animationProgress - 0.1F);
        }
    }

    public float getAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevAnimationProgress, this.animationProgress);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) this.viewerCount = 0;
            ++this.viewerCount;
            if (this.world != null) {
                this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            }
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
            if (this.world != null) {
                this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            }
        }
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1 && this.world != null) {
            this.viewerCount = data;
            if (data == 0) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            } else {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    private static final int[] SLOTS = java.util.stream.IntStream.range(0, 54).toArray();

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) {
            return false;
        }

        return !stack.isOf(ModBlocks.ETERNAL_SHULKER_BOX.asItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.canInsert(slot, stack, null);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!this.canInsert(slot, stack, null)) {
            return;
        }
        super.setStack(slot, stack);
    }
}