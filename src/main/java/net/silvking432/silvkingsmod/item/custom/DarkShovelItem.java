package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.util.TraitGenerator;

import java.util.List;

public class DarkShovelItem extends ShovelItem {
    public DarkShovelItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        List<CoreTrait> traits = stack.get(ModDataComponentTypes.CORE_TRAITS);

        if (traits != null && !traits.isEmpty()) {
            tooltip.add(Text.literal("Tool Traits:").formatted(Formatting.GOLD));

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
