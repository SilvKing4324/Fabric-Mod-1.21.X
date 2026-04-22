package net.silvking432.silvkingsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.silvking432.silvkingsmod.entity.custom.TitaniumTntEntity;
import org.jetbrains.annotations.Nullable;

public class TitaniumTntBlock extends Block {

    public static final BooleanProperty UNSTABLE = Properties.UNSTABLE;
    public static final MapCodec<TitaniumTntBlock> CODEC = createCodec(TitaniumTntBlock::new);

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    public TitaniumTntBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(UNSTABLE, false));
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && world.isReceivingRedstonePower(pos)) {
            primeTnt(world, pos, null);
            world.removeBlock(pos, false);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            primeTnt(world, pos, null);
            world.removeBlock(pos, false);
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.isCreative() && state.get(UNSTABLE)) {
            primeTnt(world, pos, player);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (!world.isClient) {
            TitaniumTntEntity tntEntity = new TitaniumTntEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, explosion.getCausingEntity());
            int i = tntEntity.getFuse();
            tntEntity.setFuse((short)(world.random.nextInt(i / 4) + i / 8));
            world.spawnEntity(tntEntity);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UNSTABLE);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world,
                                             BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE)) {
            primeTnt(world, pos, player);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
            if (stack.isOf(Items.FLINT_AND_STEEL)) {
                stack.damage(1, player, EquipmentSlot.MAINHAND);
            } else {
                stack.decrement(1);
            }
            return ItemActionResult.success(world.isClient);
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private void primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!world.isClient) {
            TitaniumTntEntity tnt = new TitaniumTntEntity(world, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, igniter);
            world.spawnEntity(tnt);
        }
    }

}
