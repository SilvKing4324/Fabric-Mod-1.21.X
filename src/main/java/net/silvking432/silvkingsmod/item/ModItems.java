package net.silvking432.silvkingsmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.item.custom.ChiselItem;
import net.silvking432.silvkingsmod.item.custom.ModFoodComponents;

public class ModItems {

    public static final Item TITANIUM_INGOT = registerItem("titanium_ingot", new Item(new Item.Settings()));
    public static final Item TITANIUM_SHARD = registerItem("titanium_shard", new Item(new Item.Settings()));
    public static final Item CHISEL = registerItem("chisel", new ChiselItem(new Item.Settings().maxDamage(32)));
    public static final Item SUPER_FLOWER = registerItem("super_flower", new Item(new Item.Settings().food(ModFoodComponents.SUPER_FLOWER)));
    public static final Item STARLIGHT_ASHES = registerItem("starlight_ashes", new Item(new Item.Settings()));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SilvKingsMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SilvKingsMod.LOGGER.info("Registering Mod Items for" + SilvKingsMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(TITANIUM_INGOT);
            entries.add(TITANIUM_SHARD);
            entries.add(CHISEL);
            entries.add(SUPER_FLOWER);
            entries.add(STARLIGHT_ASHES);

        });
    }
}
