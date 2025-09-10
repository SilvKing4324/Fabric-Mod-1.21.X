package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class TitaniumBowItem extends BowItem {
    public TitaniumBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.silvkingsmod.titanium_bow"));

        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            ItemStack ammo = player.getProjectileType(stack);

            RegistryEntryLookup<Enchantment> enchantmentLookup =
                    world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

            boolean creativeOrInfinity = player.getAbilities().creativeMode ||
                    EnchantmentHelper.getLevel(enchantmentLookup.getOrThrow(Enchantments.INFINITY), stack) > 0;



            int charge = this.getMaxUseTime(stack,user) - remainingUseTicks;
            float velocity = getPullProgress(charge); // Standard 0.0–1.0

            if (velocity < 0.1F) return;

            if (!world.isClient) {

                ArrowItem arrowItem = (ArrowItem)(ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                PersistentProjectileEntity arrow = arrowItem.createArrow(world, ammo, player, stack);

                // Geschwindigkeit erhöhen:
                arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, velocity * 5.0F, 1.0F);

                // ↑ Standard ist "*3.0F". Wenn du "*5.0F" machst, fliegt der Pfeil schneller/weiter (66%).

                if (velocity == 1.0F) {
                    arrow.setCritical(true);
                }

                stack.damage(1, player, EquipmentSlot.MAINHAND);

                world.spawnEntity(arrow);
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                    1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

            if (!creativeOrInfinity) {
                ammo.decrement(1);
                if (ammo.isEmpty()) {
                    player.getInventory().removeOne(ammo);
                }
            }
        }
    }
}
