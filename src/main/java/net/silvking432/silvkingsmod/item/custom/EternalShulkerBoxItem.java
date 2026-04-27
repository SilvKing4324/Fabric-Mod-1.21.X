package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.item.BlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;

public class EternalShulkerBoxItem extends BlockItem {
    public EternalShulkerBoxItem(Block block, Settings settings) {
        super(block, settings.maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        // Kleiner Hinweis im Spiel, dass dies eine verbesserte Box ist
        tooltip.add(Text.translatable("tooltip.silvkingsmod.eternal_shulker_box")
                .formatted(Formatting.GOLD, Formatting.ITALIC));
    }
}