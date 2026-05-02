package net.silvking432.silvkingsmod.item;

import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
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
    // region  public static final Item SUPER_FLOWER = registerItem("super_flower", new Item(new Item.Settings().food(ModFoodComponents.SUPER_FLOWER)));
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
    public static final Item TITANIUM_HELMET = registerItem("titanium_helmet", new ArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(48))));
    public static final Item TITANIUM_CHESTPLATE = registerItem("titanium_chestplate", new ModArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(48))));
    public static final Item TITANIUM_LEGGINGS = registerItem("titanium_leggings", new ArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(48))));
    public static final Item TITANIUM_BOOTS = registerItem("titanium_boots", new ArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(48))));
    public static final Item TITANIUM_HORSE_ARMOR = registerItem("titanium_horse_armor", new AnimalArmorItem(ModArmorMaterials.TITANIUM_ARMOR_MATERIAL, AnimalArmorItem.Type.EQUESTRIAN, false , new Item.Settings().maxCount(1)));
    public static final Item SILV_SMITHING_TEMPLATE = registerItem("silv_armor_trim_smithing_template", SmithingTemplateItem.of(Identifier.of(SilvKingsMod.MOD_ID, "silv"), FeatureFlags.VANILLA));
    public static final Item TITANIUM_BOW = registerItem("titanium_bow", new TitaniumBowItem(new Item.Settings().maxDamage(500)));
    public static final Item NECRON_DOOM_MUSIC_DISC = registerItem("necron_doom_music_disc", new Item(new Item.Settings().jukeboxPlayable(ModSounds.NECRON_DOOM_KEY).maxCount(1)));
    public static final Item SUPER_FLOWER_SEEDS = registerItem("super_flower_seeds", new AliasedBlockItem(ModBlocks.SUPER_FLOWER_CROP, new Item.Settings()));
    public static final Item HONEY_BERRIES = registerItem("honey_berries", new AliasedBlockItem(ModBlocks.HONEY_BERRY_BUSH, new Item.Settings().food(ModFoodComponents.HONEY_BERRY)));
    public static final Item MANTIS_SPAWN_EGG = registerItem("mantis_spawn_egg", new SpawnEggItem(ModEntities.MANTIS, 0x9dc783, 0xbfaf5f, new Item.Settings()));
    public static final Item TOMAHAWK = registerItem("tomahawk", new TomahawkItem(new Item.Settings().maxCount(16)));
    public static final Item HYPERION = registerItem("hyperion", new HyperionItem(new Item.Settings().maxCount(1)));
    public static final Item SPECTRE_STAFF = registerItem("spectre_staff", new SpectreStaffItem(new Item.Settings().maxCount(1)));
    public static final Item TITAN_PLAYER_SPAWN_EGG = registerItem("titan_player_spawn_egg", new SpawnEggItem(ModEntities.TITAN_PLAYER, 0x00daff, 0x00adff, new Item.Settings()));
    public static final Item TITAN_HEART = registerItem("titan_heart", new Item(new Item.Settings()));
    public static final Item DARK_WORLD_ORB = registerItem("dark_world_orb", new Item(new Item.Settings()));
    public static final Item DARK_WORLD_KEY = registerItem("dark_world_key", new Item(new Item.Settings()));
    public static final Item DRAGON_SCALE = registerItem("dragon_scale", new Item(new Item.Settings()));
    public static final Item DARK_TITANIUM_INGOT = registerItem("dark_titanium_ingot", new Item(new Item.Settings()));
    // region  public static final Item DARK_TITANIUM_SET = registerItem("dark_titanium_set", new DarkArmorItem(new Item.Settings()));
    public static final Item DARK_TITANIUM_HELMET = registerItem("dark_titanium_helmet", new DarkArmorItem(ModArmorMaterials.DARK_TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.HELMET,
            new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(56))){
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.silvkingsmod.titan_armor"));
            super.appendTooltip(stack, context, tooltip, type);
        }});
    public static final Item DARK_TITANIUM_CHESTPLATE = registerItem("dark_titanium_chestplate", new DarkArmorItem(ModArmorMaterials.DARK_TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.CHESTPLATE,
            new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(56))){
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.silvkingsmod.titan_armor"));
            super.appendTooltip(stack, context, tooltip, type);
        }});
    public static final Item DARK_TITANIUM_LEGGINGS = registerItem("dark_titanium_leggings", new DarkArmorItem(ModArmorMaterials.DARK_TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.LEGGINGS,
            new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(56))){
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.silvkingsmod.titan_armor"));
            super.appendTooltip(stack, context, tooltip, type);
        }});
    public static final Item DARK_TITANIUM_BOOTS = registerItem("dark_titanium_boots", new DarkArmorItem(ModArmorMaterials.DARK_TITANIUM_ARMOR_MATERIAL,ArmorItem.Type.BOOTS,
            new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(56))){
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.silvkingsmod.titan_armor"));
            super.appendTooltip(stack, context, tooltip, type);
        }});
    //endregion
    public static final Item TITANIUM_UPGRADE_TEMPLATE = registerItem("titanium_upgrade_template", TitaniumSmithingTemplateItem.createTitaniumUpgrade());
    public static final Item UNIVERSAL_UPGRADE_TEMPLATE = registerItem("universal_upgrade_template", UniversalSmithingTemplateItem.createUniversalUpgrade());
    // region  public static final Item TITANMOD_GUIDE = registerItem("titanmod_guide", new Item(new Item.Settings()));
    public static final Item TITANMOD_GUIDE = registerItem("titanmod_guide", new Item(new Item.Settings()){
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.silvkingsmod.titanmod_guide"));
            super.appendTooltip(stack, context, tooltip, type);
        }});
    //endregion
    public static final Item ETERNAL_ELYTRA = registerItem("eternal_elytra", new EternalElytraItem(new Item.Settings().maxDamage(489).rarity(Rarity.EPIC)));
    public static final Item ETERNAL_SHELL = registerItem("eternal_shell", new Item(new Item.Settings()));
    public static final Item ETERNAL_SHULKER_SPAWN_EGG = registerItem("eternal_shulker_spawn_egg", new SpawnEggItem(ModEntities.ETERNAL_SHULKER, 0xE69E10, 0xFFF200, new Item.Settings()));
    public static final Item ETERNAL_SHULKER_BOX = registerItem("eternal_shulker_box", new EternalShulkerBoxItem(ModBlocks.ETERNAL_SHULKER_BOX, new Item.Settings()));
    public static final Item DARK_SHARD_TIER1 = registerItem("dark_shard_tier1", new Item(new Item.Settings().maxCount(16)));
    public static final Item DARK_SHARD_TIER2 = registerItem("dark_shard_tier2", new Item(new Item.Settings().maxCount(16)));
    public static final Item DARK_SHARD_TIER3 = registerItem("dark_shard_tier3", new Item(new Item.Settings().maxCount(16)));
    public static final Item DARK_SHARD_TIER4 = registerItem("dark_shard_tier4", new Item(new Item.Settings().maxCount(16)));
    public static final Item DARK_SHARD_TIER5 = registerItem("dark_shard_tier5", new Item(new Item.Settings().maxCount(16)));
    public static final Item DARK_CORE_BASE = registerItem("dark_core_base", new Item(new Item.Settings().maxCount(1)));
    public static final Item DARK_CORE_TIER1 = registerItem("dark_core_tier1", new Item(new Item.Settings().maxCount(1)));
    public static final Item DARK_CORE_TIER2 = registerItem("dark_core_tier2", new Item(new Item.Settings().maxCount(1)));
    public static final Item DARK_CORE_TIER3 = registerItem("dark_core_tier3", new Item(new Item.Settings().maxCount(1)));
    public static final Item DARK_CORE_TIER4 = registerItem("dark_core_tier4", new Item(new Item.Settings().maxCount(1)));
    public static final Item DARK_CORE_TIER5 = registerItem("dark_core_tier5", new Item(new Item.Settings().maxCount(1)));
    public static final Item REFINED_WEAPON_CORE = registerItem("refined_weapon_core", new RefinedCoreItem(new Item.Settings().maxCount(1)));
    public static final Item REFINED_ARMOR_CORE = registerItem("refined_armor_core", new RefinedCoreItem(new Item.Settings().maxCount(1)));
    public static final Item REFINED_TOOL_CORE = registerItem("refined_tool_core", new RefinedCoreItem(new Item.Settings().maxCount(1)));
    public static final Item DARK_TITANIUM_SWORD = registerItem("dark_titanium_sword", new DarkSwordItem(ModToolMaterials.DARK_TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.DARK_TITANIUM,3,-2.4f))));
    public static final Item DARK_TITANIUM_PICKAXE = registerItem("dark_titanium_pickaxe", new DarkPickaxeItem(ModToolMaterials.DARK_TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.DARK_TITANIUM,1,-2.8f))));
    public static final Item DARK_TITANIUM_AXE = registerItem("dark_titanium_axe", new DarkAxeItem(ModToolMaterials.DARK_TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.DARK_TITANIUM,6,-3.0f))));
    public static final Item DARK_TITANIUM_SHOVEL = registerItem("dark_titanium_shovel", new DarkShovelItem(ModToolMaterials.DARK_TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.DARK_TITANIUM,2,-3.0f))));
    public static final Item DARK_TITANIUM_HOE = registerItem("dark_titanium_hoe", new DarkHoeItem(ModToolMaterials.DARK_TITANIUM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.DARK_TITANIUM,0,-3.0f))));
    public static final Item RAW_RHINO_STEAK = registerItem("raw_rhino_steak", new Item(new Item.Settings().food(ModFoodComponents.RAW_RHINO_STEAK)));
    public static final Item RHINO_STEAK = registerItem("rhino_steak", new Item(new Item.Settings().food(ModFoodComponents.RHINO_STEAK)));



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SilvKingsMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SilvKingsMod.LOGGER.info("Registering Mod Items for" + SilvKingsMod.MOD_ID);
    }
}
