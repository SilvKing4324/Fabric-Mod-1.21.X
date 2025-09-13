package net.silvking432.silvkingsmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.item.custom.*;
import net.silvking432.silvkingsmod.sound.ModSounds;

import java.util.List;

public class ModItems {

    public static final Item TITANIUM_INGOT = registerItem("titanium_ingot", new Item(new Item.Settings()));
    public static final Item TITANIUM_SHARD = registerItem("titanium_shard", new Item(new Item.Settings()));
    public static final Item CHISEL = registerItem("chisel", new ChiselItem(new Item.Settings().maxDamage(32)));
    // region  public static final Item SUPER_FLOWER = registerItem("super_flower", new Item(new Item.Settings().food(ModFoodComponents.SUPER_FLOWER))
    public static final Item SUPER_FLOWER = registerItem("super_flower", new Item(new Item.Settings().food(ModFoodComponents.SUPER_FLOWER)) {
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.silvkingsmod.super_flower"));
        super.appendTooltip(stack, context, tooltip, type);
    }});
    // endregion
    public static final Item STARLIGHT_ASHES = registerItem("starlight_ashes", new Item(new Item.Settings()));
    public static final Item TITANIUM_SWORD = registerItem("titanium_sword", new SwordItem(ModToolMaterials.TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.TITANIUM,3,-2.4f))));
    public static final Item TITANIUM_PICKAXE = registerItem("titanium_pickaxe", new PickaxeItem(ModToolMaterials.TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.TITANIUM,1,-2.8f))));
    public static final Item TITANIUM_AXE = registerItem("titanium_axe", new AxeItem(ModToolMaterials.TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.TITANIUM,6,-3.0f))));
    public static final Item TITANIUM_SHOVEL = registerItem("titanium_shovel", new ShovelItem(ModToolMaterials.TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.TITANIUM,2,-3.0f))));
    public static final Item TITANIUM_HOE = registerItem("titanium_hoe", new HoeItem(ModToolMaterials.TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.TITANIUM,0,-3.0f))));
    public static final Item TITANIUM_HAMMER = registerItem("titanium_hammer", new HammerItem(ModToolMaterials.TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.TITANIUM,7,-3.4f))));
    public static final Item TITANIUM_HELMET = registerItem("titanium_helmet", new ArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(43))));
    public static final Item TITANIUM_CHESTPLATE = registerItem("titanium_chestplate", new ModArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(43))));
    public static final Item TITANIUM_LEGGINGS = registerItem("titanium_leggings", new ArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(43))));
    public static final Item TITANIUM_BOOTS = registerItem("titanium_boots", new ArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(43))));
    public static final Item TITANIUM_HORSE_ARMOR = registerItem("titanium_horse_armor", new AnimalArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL, AnimalArmorItem.Type.EQUESTRIAN, false , new Item.Settings().maxCount(1)));
    public static final Item SILV_SMITHING_TEMPLATE = registerItem("silv_armor_trim_smithing_template", SmithingTemplateItem.of(Identifier.of(SilvKingsMod.MOD_ID, "silv"), FeatureFlags.VANILLA));
    public static final Item TITANIUM_BOW = registerItem("titanium_bow", new TitaniumBowItem(new Item.Settings().maxDamage(500)));
    public static final Item NECRON_DOOM_MUSIC_DISC = registerItem("necron_doom_music_disc", new Item(new Item.Settings().jukeboxPlayable(ModSounds.NECRON_DOOM_KEY).maxCount(1)));
    public static final Item SUPER_FLOWER_SEEDS = registerItem("super_flower_seeds", new AliasedBlockItem(ModBlocks.SUPER_FLOWER_CROP, new Item.Settings()));
    public static final Item HONEY_BERRIES = registerItem("honey_berries", new AliasedBlockItem(ModBlocks.HONEY_BERRY_BUSH, new Item.Settings().food(ModFoodComponents.HONEY_BERRY)));
    public static final Item MANTIS_SPAWN_EGG = registerItem("mantis_spawn_egg", new SpawnEggItem(ModEntities.MANTIS, 0x9dc783, 0xbfaf5f, new Item.Settings()));
    public static final Item TOMAHAWK = registerItem("tomahawk", new TomahawkItem(new Item.Settings().maxCount(16)));
    public static final Item HYPERION = registerItem("hyperion", new Item(new Item.Settings().maxCount(1)));
    public static final Item SPECTRE_STAFF = registerItem("spectre_staff", new Item(new Item.Settings().maxCount(1)));
    public static final Item TITAN_PLAYER_SPAWN_EGG = registerItem("titan_player_spawn_egg", new SpawnEggItem(ModEntities.TITAN_PLAYER, 0x00daff, 0x00adff, new Item.Settings()));


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
