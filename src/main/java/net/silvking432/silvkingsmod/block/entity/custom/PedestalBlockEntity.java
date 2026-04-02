package net.silvking432.silvkingsmod.block.entity.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.custom.PedestalBlock;
import net.silvking432.silvkingsmod.block.entity.ImplementedInventory;
import net.silvking432.silvkingsmod.block.entity.ModBlockEntities;
import net.silvking432.silvkingsmod.screen.custom.PedestalScreenHandler;
import org.jetbrains.annotations.Nullable;

public class PedestalBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1,ItemStack.EMPTY);
    private float rotation = 0;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEDESTAL_BE, pos, state);
    }


    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Pedestal");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new PedestalScreenHandler(syncId, playerInventory, this.pos);
    }

    public void updatePortalState(World world, BlockPos pedestalPos, boolean active) {
        if (world.isClient()) return;

        BlockState state = world.getBlockState(pedestalPos);
        if (!(state.getBlock() instanceof PedestalBlock)) return;

        Direction facing = state.get(PedestalBlock.FACING);

        // TODO: Fix Portal Position for some Ancient Citys

        Direction forward = facing;
        Direction side = facing.rotateYClockwise();


        BlockPos origin = pedestalPos.offset(forward, 5).up(8);

        int leftWidth = 9;
        int rightWidth = 10;
        int height = 6;

        for (int s = -leftWidth; s <= rightWidth; s++) {
            for (int y = 0; y < height; y++) {
                BlockPos target = origin.offset(side, s).up(y);

                if (active) {
                    world.setBlockState(target, ModBlocks.DARK_WORLD_PORTAL.getDefaultState());
                } else {
                    if (world.getBlockState(target).isOf(ModBlocks.DARK_WORLD_PORTAL)) {
                        world.setBlockState(target, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }
}
