package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class GuideBookItem extends Item {
    public static final String WIKI_URL = "https://github.com/SilvKing4324/Fabric-Mod-1.21.X/wiki";

    public GuideBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            user.sendMessage(Text.literal("Thank you for playing this mod!")
                    .formatted(Formatting.LIGHT_PURPLE), false);

            user.sendMessage(Text.literal("The Guide Book is currently not finished.")
                    .formatted(Formatting.GRAY), false);

            user.sendMessage(Text.literal("If you are looking for Information visit the wiki:")
                    .formatted(Formatting.GRAY), false);

            Text wikiLink = Text.literal("Click here to visit our Wiki: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(WIKI_URL)
                            .setStyle(Style.EMPTY
                                    .withColor(Formatting.AQUA)
                                    .withUnderline(true)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WIKI_URL))));

            user.sendMessage(wikiLink, false);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("This item is not yet fully implemented.")
                .formatted(Formatting.RED, Formatting.ITALIC));

        tooltip.add(Text.literal("Wiki URL:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal(WIKI_URL)
                .setStyle(Style.EMPTY
                        .withColor(Formatting.AQUA)
                        .withUnderline(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WIKI_URL))));
    }
}