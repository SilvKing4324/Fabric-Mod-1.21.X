package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.particle.ModParticles;
import net.silvking432.silvkingsmod.sound.ModSounds;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChiselItem extends Item {
    private static final Map<Block, Block> CHISEL_MAP =
            Map.of(
                    Blocks.STONE, Blocks.STONE_BRICKS,
                    Blocks.END_STONE, Blocks.END_STONE_BRICKS,
                    Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS,
                    Blocks.SANDSTONE, Blocks.CUT_SANDSTONE,
                    Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE,
                    Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE
            );

    public ChiselItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        Block clickedBlock = world.getBlockState(context.getBlockPos()).getBlock();

        if (CHISEL_MAP.containsKey(clickedBlock)) {
            if (!world.isClient()) {
                world.setBlockState(context.getBlockPos(), CHISEL_MAP.get(clickedBlock).getDefaultState());

                context.getStack().damage(1,((ServerWorld) world), ((ServerPlayerEntity) context.getPlayer()),
                        item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));

                world.playSound(null,context.getBlockPos(), ModSounds.CHISEL_USE, SoundCategory.BLOCKS);

                ((ServerWorld) world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, clickedBlock.getDefaultState()),
                        context.getBlockPos().getX() + 0.5, context.getBlockPos().getY() + 0.5, context.getBlockPos().getZ() + 0.5, 20, 0.7, 0.7, 0.7,0.1);

                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME,
                        context.getBlockPos().getX() + 0.5, context.getBlockPos().getY() + 1.0, context.getBlockPos().getZ() + 0.5, 20, 0, 0, 0,0.1);

                ((ServerWorld) world).spawnParticles(ModParticles.STARLIGHT_ASHES_PARTICLE,
                        context.getBlockPos().getX() + 0.5, context.getBlockPos().getY() + 1.0, context.getBlockPos().getZ() + 0.5, 20, 0, 0, 0,0.1);

                context.getStack().set(ModDataComponentTypes.COORDINATES, context.getBlockPos());
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.silvkingsmod.chisel.shift_down"));
        } else {
            tooltip.add(Text.translatable("tooltip.silvkingsmod.chisel"));
        }

        if (stack.get(ModDataComponentTypes.COORDINATES) != null) {
            tooltip.add(Text.literal("Last Block Changed at " + stack.get(ModDataComponentTypes.COORDINATES)));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
