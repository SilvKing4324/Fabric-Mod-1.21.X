package net.silvking432.silvkingsmod.screen.custom;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.world.WorldEvents;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.item.custom.*;
import net.silvking432.silvkingsmod.network.BlockPosPayload;
import net.silvking432.silvkingsmod.screen.ModScreenHandlers;

import java.util.ArrayList;
import java.util.List;

public class DarkAnvilScreenHandler extends ForgingScreenHandler {
    private final Property levelCost = Property.create();
    private String newItemName;
    private static final List<Item> ALLOWED_INPUT_ITEMS = List.of();
    public DarkAnvilScreenHandler(int syncId, PlayerInventory inventory, BlockPosPayload payload) {
        this(syncId, inventory, ScreenHandlerContext.create(inventory.player.getWorld(), payload.pos()));
    }

    public DarkAnvilScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.DARK_ANVIL_SCREEN_HANDLER, syncId, inventory, context);
        this.addProperty(this.levelCost);
    }

    @Override
    protected ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.create()
                .input(0, 27, 47, stack ->
                        stack.getItem() instanceof DarkArmorItem ||
                                stack.getItem() instanceof DarkSwordItem ||
                                stack.getItem() instanceof ToolItem ||
                                ALLOWED_INPUT_ITEMS.contains(stack.getItem())
                )
                .input(1, 76, 47, stack -> {
                    if (!stack.contains(ModDataComponentTypes.CORE_TRAITS)) return false;

                    Item coreItem = stack.getItem();
                    ItemStack leftStack = this.input.getStack(0);

                    if (leftStack.isEmpty()) {
                        return coreItem == ModItems.REFINED_ARMOR_CORE ||
                                coreItem == ModItems.REFINED_TOOL_CORE ||
                                coreItem == ModItems.REFINED_WEAPON_CORE;
                    }

                    Item leftItem = leftStack.getItem();
                    if (leftItem instanceof DarkArmorItem) return coreItem == ModItems.REFINED_ARMOR_CORE;
                    if (leftItem instanceof DarkSwordItem) return coreItem == ModItems.REFINED_WEAPON_CORE;
                    if (leftItem instanceof ToolItem) return coreItem == ModItems.REFINED_TOOL_CORE;

                    return false;
                })
                .output(2, 134, 47).build();
    }

    @Override
    protected boolean canUse(BlockState state) {
        return true;
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return (player.getAbilities().creativeMode || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            player.addExperienceLevels(-this.levelCost.get());
        }

        this.input.setStack(0, ItemStack.EMPTY);
        this.input.getStack(1).decrement(1);

        this.levelCost.set(0);
        this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0));
    }

    public void updateResult() {
        ItemStack leftStack = this.input.getStack(0);
        ItemStack rightStack = this.input.getStack(1);

        if (leftStack.isEmpty() || rightStack.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }

        Item leftItem = leftStack.getItem();
        Item coreItem = rightStack.getItem();

        boolean isBaseValid = (leftItem instanceof DarkArmorItem || leftItem instanceof DarkSwordItem ||
                leftItem instanceof DarkAxeItem || leftItem instanceof DarkPickaxeItem || leftItem instanceof DarkShovelItem ||
                leftItem instanceof DarkHoeItem || ALLOWED_INPUT_ITEMS.contains(leftItem))
                && rightStack.contains(ModDataComponentTypes.CORE_TRAITS);

        if (!isBaseValid) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }

        boolean typeMatches = false;
        if (leftItem instanceof DarkArmorItem && coreItem == ModItems.REFINED_ARMOR_CORE) typeMatches = true;
        else if (leftItem instanceof DarkSwordItem && coreItem == ModItems.REFINED_WEAPON_CORE) typeMatches = true;
        else if (leftItem instanceof ToolItem && !(leftItem instanceof DarkSwordItem) && coreItem == ModItems.REFINED_TOOL_CORE) typeMatches = true;

        if (!typeMatches) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }

            ItemStack resultStack = leftStack.copy();
            List<CoreTrait> coreTraits = rightStack.get(ModDataComponentTypes.CORE_TRAITS);

            if (coreTraits != null && !coreTraits.isEmpty()) {
                resultStack.set(ModDataComponentTypes.CORE_TRAITS, new ArrayList<>(coreTraits));

                float totalSpeedBonus = 0.0f;
                for (CoreTrait trait : coreTraits) {
                    if (trait.traitId().equals("EXTRA_ATTACK_SPEED")) {
                        totalSpeedBonus += trait.value();
                    }
                }

                float extraDurability = 0.0f;
                for (CoreTrait trait : coreTraits) {
                    if (trait.traitId().equals("EXTRA_DURABILITY")) {
                        extraDurability += trait.value();
                    }
                }
                float miningSpeedBonus = 0.0f;
                for (CoreTrait trait : coreTraits) {
                    if (trait.traitId().equals("FAST_MINER")) {
                        miningSpeedBonus += trait.value();
                    }
                }
                resultStack.remove(DataComponentTypes.MAX_DAMAGE);


                AttributeModifiersComponent currentModifiers = resultStack.getOrDefault(
                        DataComponentTypes.ATTRIBUTE_MODIFIERS,
                        resultStack.getItem().getAttributeModifiers()
                );

                AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
                Identifier traitModifierId = Identifier.of(SilvKingsMod.MOD_ID, "trait_attack_speed");
                Identifier durabilityModifierId = Identifier.of(SilvKingsMod.MOD_ID, "trait_durability");
                Identifier miningModifierId = Identifier.of(SilvKingsMod.MOD_ID, "trait_mining_speed");

                for (AttributeModifiersComponent.Entry entry : currentModifiers.modifiers()) {
                    if (!entry.modifier().id().equals(traitModifierId) && !entry.modifier().id().equals(durabilityModifierId) && !entry.modifier().id().equals(miningModifierId)) {
                        builder.add(entry.attribute(), entry.modifier(), entry.slot());
                    }
                }

                if (totalSpeedBonus > 0) {
                    builder.add(
                            EntityAttributes.GENERIC_ATTACK_SPEED,
                            new EntityAttributeModifier(
                                    traitModifierId,
                                    totalSpeedBonus / 100.0,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            AttributeModifierSlot.MAINHAND
                    );
                }

                if (extraDurability > 0) {
                    int baseMaxDamage = leftStack.getItem().getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0);

                    if (baseMaxDamage > 0) {
                        int newMaxDamage = baseMaxDamage + (int)extraDurability;
                        resultStack.set(DataComponentTypes.MAX_DAMAGE, newMaxDamage);
                        int currentDamage = leftStack.getOrDefault(DataComponentTypes.DAMAGE, 0);
                        int newDamage = Math.max(0, currentDamage - (int)extraDurability);
                        resultStack.set(DataComponentTypes.DAMAGE, newDamage);
                    }
                }

                if (miningSpeedBonus > 0) {
                    builder.add(
                            EntityAttributes.PLAYER_MINING_EFFICIENCY,
                            new EntityAttributeModifier(
                                    miningModifierId,
                                    miningSpeedBonus / 100.0,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            AttributeModifierSlot.MAINHAND
                    );
                }

                resultStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

                if (!StringHelper.isBlank(this.newItemName)) {
                    resultStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                }

                this.levelCost.set(1);
                this.output.setStack(0, resultStack);
            } else {
                this.output.setStack(0, ItemStack.EMPTY);
                this.levelCost.set(0);
            }
        this.sendContentUpdates();
    }

    public void setNewItemName(String name) {
        this.newItemName = name;
        this.updateResult();
    }

    public int getLevelCost() {
        return this.levelCost.get();
    }
}