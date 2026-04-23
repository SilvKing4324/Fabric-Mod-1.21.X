package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class EternalElytraItem extends ElytraItem {
    public EternalElytraItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return super.canRepair(stack, ingredient);
    }
}