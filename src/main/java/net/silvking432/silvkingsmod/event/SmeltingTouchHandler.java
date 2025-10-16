package net.silvking432.silvkingsmod.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.enchantment.ModEnchantments;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.Map;
import java.util.Objects;

public class SmeltingTouchHandler {

    // Welche Blöcke „geschmolzen“ werden
    private static final Map<Block, ItemStack> SMELTING_MAP = Map.ofEntries(
            Map.entry(Blocks.IRON_ORE, new ItemStack(Items.IRON_INGOT)),
            Map.entry(Blocks.DEEPSLATE_IRON_ORE, new ItemStack(Items.IRON_INGOT)),
            Map.entry(Blocks.GOLD_ORE, new ItemStack(Items.GOLD_INGOT)),
            Map.entry(Blocks.DEEPSLATE_GOLD_ORE, new ItemStack(Items.GOLD_INGOT)),
            Map.entry(Blocks.COPPER_ORE, new ItemStack(Items.COPPER_INGOT)),
            Map.entry(Blocks.DEEPSLATE_COPPER_ORE, new ItemStack(Items.COPPER_INGOT)),
            Map.entry(Blocks.ANCIENT_DEBRIS, new ItemStack(Items.NETHERITE_SCRAP)),
            Map.entry(ModBlocks.TITANIUM_ORE, new ItemStack(ModItems.TITANIUM_INGOT)),
            Map.entry(ModBlocks.TITANIUM_DEEPSLATE_ORE, new ItemStack(ModItems.TITANIUM_INGOT)),
            Map.entry(ModBlocks.TITANIUM_NETHER_ORE, new ItemStack(ModItems.TITANIUM_INGOT)),
            Map.entry(ModBlocks.TITANIUM_END_ORE, new ItemStack(ModItems.TITANIUM_INGOT))
    );

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return true; // Client ignorieren
            if (!(world instanceof ServerWorld serverWorld)) return true;

            var enchantmentRegistry = serverWorld.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
            RegistryEntry<Enchantment> smeltingEntry = enchantmentRegistry.getEntry(ModEnchantments.SMELTING_TOUCH).orElse(null);
            if (smeltingEntry == null) return true;

            int level = EnchantmentHelper.getLevel(smeltingEntry, player.getMainHandStack());
            if (level <= 0) return true;

            Block block = state.getBlock();
            ItemStack result = SMELTING_MAP.get(block);

            if (result != null && !player.isInCreativeMode()) {
                // Eigene Drops
                Block.dropStack(world, pos, result.copy());

                // Block entfernen, Standard-Drops verhindern
                world.removeBlock(pos, false);
                if (world instanceof ServerWorld) {
                    serverWorld.spawnParticles(
                            ParticleTypes.FLAME,   // Partikeltyp
                            pos.getX() + 0.5,      // X
                            pos.getY() + 1.0,      // Y (über dem Block)
                            pos.getZ() + 0.5,      // Z
                            20,                    // Anzahl Partikel
                            0.2, 0.2, 0.2,         // Spread X/Y/Z
                            0.05                   // Geschwindigkeit
                    );

                }
                world.playSound(
                        null, // nur der Spieler hört den Sound
                        pos,
                        SoundEvents.ENTITY_ENDER_DRAGON_SHOOT,
                        SoundCategory.BLOCKS,
                        1.0f,
                        1.0f
                );
                ItemStack stack = player.getMainHandStack();
                stack.damage(1,((ServerWorld) world), ((ServerPlayerEntity) player),
                        item -> Objects.requireNonNull(player).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));


                return false; // Verhindert die normalen Drops
            }

            return true;
        });
    }

}
