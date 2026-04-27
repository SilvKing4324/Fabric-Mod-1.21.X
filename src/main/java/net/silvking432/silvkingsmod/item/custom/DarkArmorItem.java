package net.silvking432.silvkingsmod.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.item.ModArmorMaterials;
import net.silvking432.silvkingsmod.util.TraitGenerator;

import java.util.List;
import java.util.Map;

public class DarkArmorItem extends ArmorItem {
    private int tickCounter = 0;

    private static final Map<RegistryEntry<ArmorMaterial>, List<StatusEffectInstance>> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<RegistryEntry<ArmorMaterial>, List<StatusEffectInstance>>())
                    .put(ModArmorMaterials.DARK_TITANIUM_ARMOR_MATERIAL,
                            List.of(new StatusEffectInstance(StatusEffects.SPEED, 400, 1, false, false),
                                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 400, 0, false, false),
                                    new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 400, 0, false, false))).build();

    public DarkArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, applyDarkAttributes(material, type, settings));
    }

    private static Settings applyDarkAttributes(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        AttributeModifierSlot slot = switch (type) {
            case HELMET -> AttributeModifierSlot.HEAD;
            case CHESTPLATE -> AttributeModifierSlot.CHEST;
            case LEGGINGS -> AttributeModifierSlot.LEGS;
            case BOOTS -> AttributeModifierSlot.FEET;
            default -> AttributeModifierSlot.ANY;
        };

        String p = type.getName() + "_";

        float protection = material.value().getProtection(type);
        float toughness = material.value().toughness();
        float knockbackResistance = material.value().knockbackResistance();

        builder.add(EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier(Identifier.of(SilvKingsMod.MOD_ID, p + "armor"), protection, EntityAttributeModifier.Operation.ADD_VALUE), slot);
        builder.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                new EntityAttributeModifier(Identifier.of(SilvKingsMod.MOD_ID, p + "toughness"), toughness, EntityAttributeModifier.Operation.ADD_VALUE), slot);

        if (knockbackResistance > 0) {
            builder.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                    new EntityAttributeModifier(Identifier.of(SilvKingsMod.MOD_ID, p + "knockback"), knockbackResistance, EntityAttributeModifier.Operation.ADD_VALUE), slot);
        }

        if (material.equals(ModArmorMaterials.DARK_TITANIUM_ARMOR_MATERIAL)) {
            switch (type) {
                case HELMET -> builder.add(EntityAttributes.GENERIC_OXYGEN_BONUS,
                        new EntityAttributeModifier(Identifier.of(SilvKingsMod.MOD_ID, p + "oxygen"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE), slot);

                case CHESTPLATE -> builder.add(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(Identifier.of(SilvKingsMod.MOD_ID, p + "atk_speed"), 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), slot);

                case LEGGINGS -> builder.add(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                        new EntityAttributeModifier(Identifier.of(SilvKingsMod.MOD_ID, p + "mov_speed"), 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), slot);

                case BOOTS -> builder.add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE,
                        new EntityAttributeModifier(Identifier.of(SilvKingsMod.MOD_ID, p + "fall_dist"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE), slot);
            }
        }
        return settings.attributeModifiers(builder.build());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient()) {
            if(entity instanceof PlayerEntity player) {
                if (isChestplate(stack)) {
                    if(hasFullSuitOfArmorOn(player)) {
                        evaluateArmorEffects(player);
                        tickCounter++;
                        if (tickCounter >= 1200) {
                            applySaturationEffect(player);
                            tickCounter = 0;
                        }
                    } else {
                        tickCounter = 0;
                    }
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void applySaturationEffect(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 2, 0, false, false));
    }

    private boolean isChestplate(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getType() == Type.CHESTPLATE;
        }
        return false;
    }

    private void evaluateArmorEffects(PlayerEntity player) {
        for (Map.Entry<RegistryEntry<ArmorMaterial>, List<StatusEffectInstance>> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            RegistryEntry<ArmorMaterial> mapArmorMaterial = entry.getKey();
            List<StatusEffectInstance> mapStatusEffects = entry.getValue();

            if(hasCorrectArmorOn(mapArmorMaterial, player)) {
                addStatusEffectForMaterial(player, mapArmorMaterial, mapStatusEffects);
            }
        }
    }

    private void addStatusEffectForMaterial(PlayerEntity player, RegistryEntry<ArmorMaterial> mapArmorMaterial, List<StatusEffectInstance> mapStatusEffect) {
        boolean hasPlayerEffect = mapStatusEffect.stream().allMatch(statusEffectInstance -> player.hasStatusEffect(statusEffectInstance.getEffectType()));

        if(!hasPlayerEffect) {
            for (StatusEffectInstance instance : mapStatusEffect) {
                player.addStatusEffect(new StatusEffectInstance(instance.getEffectType(),
                        instance.getDuration(), instance.getAmplifier(), instance.isAmbient(), instance.shouldShowParticles()));
            }
        }
    }

    private boolean hasFullSuitOfArmorOn(PlayerEntity player) {
        ItemStack boots = player.getInventory().getArmorStack(0);
        ItemStack leggings = player.getInventory().getArmorStack(1);
        ItemStack breastplate = player.getInventory().getArmorStack(2);
        ItemStack helmet = player.getInventory().getArmorStack(3);

        return !helmet.isEmpty() && !breastplate.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private boolean hasCorrectArmorOn(RegistryEntry<ArmorMaterial> material, PlayerEntity player) {
        for (ItemStack armorStack: player.getInventory().armor) {
            if(!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem boots = ((ArmorItem)player.getInventory().getArmorStack(0).getItem());
        ArmorItem leggings = ((ArmorItem)player.getInventory().getArmorStack(1).getItem());
        ArmorItem breastplate = ((ArmorItem)player.getInventory().getArmorStack(2).getItem());
        ArmorItem helmet = ((ArmorItem)player.getInventory().getArmorStack(3).getItem());

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        List<CoreTrait> traits = stack.get(ModDataComponentTypes.CORE_TRAITS);

        if (traits != null && !traits.isEmpty()) {
            tooltip.add(Text.literal("Armor Traits:").formatted(Formatting.GOLD));

            for (CoreTrait trait : traits) {
                String traitId = trait.traitId();

                if (traitId.startsWith("SKILL_")) {
                    String skillKey = traitId.replace("SKILL_", "");
                    try {
                        TraitGenerator.SkillTraitType skillType = TraitGenerator.SkillTraitType.valueOf(skillKey);
                        tooltip.add(Text.literal(" ★ ")
                                .append(Text.literal(skillType.displayName).formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD))
                                .append(Text.literal(skillType.suffix).formatted(Formatting.DARK_PURPLE)));
                    } catch (IllegalArgumentException e) {
                        tooltip.add(Text.literal("Unknown Skill: " + skillKey).formatted(Formatting.RED));
                    }
                } else {
                    try {
                        TraitGenerator.TraitType tType = TraitGenerator.TraitType.valueOf(traitId);
                        tooltip.add(Text.literal(" - ")
                                .append(Text.literal(tType.displayName).formatted(Formatting.GRAY))
                                .append(Text.literal(" +" + trait.value() + tType.unit).formatted(Formatting.BLUE)));
                    } catch (IllegalArgumentException e) {
                        tooltip.add(Text.literal("Unknown Trait: " + traitId).formatted(Formatting.RED));
                    }
                }
            }
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}