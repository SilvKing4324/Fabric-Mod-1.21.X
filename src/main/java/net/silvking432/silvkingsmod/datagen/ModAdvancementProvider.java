package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.*;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureKeys;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.dimension.ModDimensions;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.world.gen.ModStructures;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraft.data.server.advancement.vanilla.VanillaHusbandryTabAdvancementGenerator.EGG_LAYING_ANIMALS;

public class ModAdvancementProvider extends FabricAdvancementProvider {

    protected static final List<EntityType<?>> MONSTERS = Arrays.asList(
            EntityType.BLAZE,
            EntityType.BOGGED,
            EntityType.BREEZE,
            EntityType.CAVE_SPIDER,
            EntityType.CREEPER,
            EntityType.DROWNED,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.GHAST,
            EntityType.GUARDIAN,
            EntityType.HOGLIN,
            EntityType.HUSK,
            EntityType.MAGMA_CUBE,
            EntityType.PHANTOM,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.PILLAGER,
            EntityType.RAVAGER,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SLIME,
            EntityType.SPIDER,
            EntityType.STRAY,
            EntityType.VEX,
            EntityType.VINDICATOR,
            EntityType.WITCH,
            EntityType.WITHER_SKELETON,
            EntityType.WITHER,
            EntityType.ZOGLIN,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIE,
            EntityType.ZOMBIFIED_PIGLIN,
            ModEntities.ABYSSAL_SHADOW,
            ModEntities.DARK_SHADOW,
            ModEntities.ETERNAL_SHULKER,
            ModEntities.LAVA_GOLEM,
            ModEntities.MAGNA_TITAN,
            ModEntities.MAGNA_MINION,
            ModEntities.MAGNA_WITCH,
            ModEntities.NECRO_BEE,
            ModEntities.NECRO_CHICKEN,
            ModEntities.NECRO_COW,
            ModEntities.NECRO_GECKO,
            ModEntities.NECRO_LLAMA,
            ModEntities.NECRO_MINI_CHICKEN,
            ModEntities.NECRO_PIG,
            ModEntities.NECRO_SHEEP,
            ModEntities.NECRO_WOLF,
            ModEntities.NECROSAURUS,
            ModEntities.TITAN_PLAYER
    );

    public static final List<EntityType<?>> BREEDABLE_ANIMALS = List.of(
            EntityType.HORSE,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.SHEEP,
            EntityType.COW,
            EntityType.MOOSHROOM,
            EntityType.PIG,
            EntityType.CHICKEN,
            EntityType.WOLF,
            EntityType.OCELOT,
            EntityType.RABBIT,
            EntityType.LLAMA,
            EntityType.CAT,
            EntityType.PANDA,
            EntityType.FOX,
            EntityType.BEE,
            EntityType.HOGLIN,
            EntityType.STRIDER,
            EntityType.GOAT,
            EntityType.AXOLOTL,
            EntityType.CAMEL,
            EntityType.ARMADILLO,
            ModEntities.GECKO,
            ModEntities.MANTIS,
            ModEntities.RHINO
    );

    private static final Item[] FOOD_ITEMS = new Item[]{
            Items.APPLE,
            Items.MUSHROOM_STEW,
            Items.BREAD,
            Items.PORKCHOP,
            Items.COOKED_PORKCHOP,
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.COD,
            Items.SALMON,
            Items.TROPICAL_FISH,
            Items.PUFFERFISH,
            Items.COOKED_COD,
            Items.COOKED_SALMON,
            Items.COOKIE,
            Items.MELON_SLICE,
            Items.BEEF,
            Items.COOKED_BEEF,
            Items.CHICKEN,
            Items.COOKED_CHICKEN,
            Items.ROTTEN_FLESH,
            Items.SPIDER_EYE,
            Items.CARROT,
            Items.POTATO,
            Items.BAKED_POTATO,
            Items.POISONOUS_POTATO,
            Items.GOLDEN_CARROT,
            Items.PUMPKIN_PIE,
            Items.RABBIT,
            Items.COOKED_RABBIT,
            Items.RABBIT_STEW,
            Items.MUTTON,
            Items.COOKED_MUTTON,
            Items.CHORUS_FRUIT,
            Items.BEETROOT,
            Items.BEETROOT_SOUP,
            Items.DRIED_KELP,
            Items.SUSPICIOUS_STEW,
            Items.SWEET_BERRIES,
            Items.HONEY_BOTTLE,
            Items.GLOW_BERRIES,
            ModItems.SUPER_FLOWER,
            ModItems.HONEY_BERRIES,
            ModItems.RAW_RHINO_STEAK,
            ModItems.RHINO_STEAK,
    };

    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> exporter) {

        // region Story

        AdvancementEntry sadvancementEntry = Advancement.Builder.create()
                .display(
                        Blocks.GRASS_BLOCK,
                        Text.translatable("advancements.story.root.title"),
                        Text.translatable("advancements.story.root.description"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/stone.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Blocks.CRAFTING_TABLE))
                .build(exporter, "story/root");

        AdvancementEntry sadvancementEntry2 = Advancement.Builder.create()
                .parent(sadvancementEntry)
                .display(
                        Items.WOODEN_PICKAXE,
                        Text.translatable("advancements.story.mine_stone.title"),
                        Text.translatable("advancements.story.mine_stone.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("get_stone", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(ItemTags.STONE_TOOL_MATERIALS)))
                .build(exporter, "story/mine_stone");

        AdvancementEntry sadvancementEntry3 = Advancement.Builder.create()
                .parent(sadvancementEntry2)
                .display(
                        Items.STONE_PICKAXE,
                        Text.translatable("advancements.story.upgrade_tools.title"),
                        Text.translatable("advancements.story.upgrade_tools.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("stone_pickaxe", InventoryChangedCriterion.Conditions.items(Items.STONE_PICKAXE))
                .build(exporter, "story/upgrade_tools");

        AdvancementEntry sadvancementEntry4 = Advancement.Builder.create()
                .parent(sadvancementEntry3)
                .display(
                        Items.IRON_INGOT,
                        Text.translatable("advancements.story.smelt_iron.title"),
                        Text.translatable("advancements.story.smelt_iron.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("iron", InventoryChangedCriterion.Conditions.items(Items.IRON_INGOT))
                .build(exporter, "story/smelt_iron");

        AdvancementEntry sadvancementEntry5 = Advancement.Builder.create()
                .parent(sadvancementEntry4)
                .display(
                        Items.IRON_PICKAXE,
                        Text.translatable("advancements.story.iron_tools.title"),
                        Text.translatable("advancements.story.iron_tools.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("iron_pickaxe", InventoryChangedCriterion.Conditions.items(Items.IRON_PICKAXE))
                .build(exporter, "story/iron_tools");


        AdvancementEntry sadvancementEntry6 = Advancement.Builder.create()
                .parent(sadvancementEntry5)
                .display(
                        Items.DIAMOND,
                        Text.translatable("advancements.story.mine_diamond.title"),
                        Text.translatable("advancements.story.mine_diamond.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("diamond", InventoryChangedCriterion.Conditions.items(Items.DIAMOND))
                .build(exporter, "story/mine_diamond");

        AdvancementEntry sadvancementEntry7 = Advancement.Builder.create()
                .parent(sadvancementEntry6)
                .display(
                        Items.DIAMOND_PICKAXE,
                        Text.translatable("advancements.silvkingsmod.story.diamond_tools.title"),
                        Text.translatable("advancements.silvkingsmod.story.diamond_tools.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("diamond_pickaxe", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_PICKAXE))
                .build(exporter, "story/diamond_tools");

        AdvancementEntry sadvancementEntry8 = Advancement.Builder.create()
                .parent(sadvancementEntry7)
                .display(
                        ModItems.TITANIUM_INGOT,
                        Text.translatable("advancements.silvkingsmod.story.smelt_titanium.title"),
                        Text.translatable("advancements.silvkingsmod.story.smelt_titanium.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("titanium_ingot", InventoryChangedCriterion.Conditions.items(ModItems.TITANIUM_INGOT))
                .build(exporter, "story/obtain_titanium");



        // endregion

        // region Adventure

        AdvancementEntry advancementEntry = Advancement.Builder.create()
                .display(
                        Items.MAP,
                        Text.translatable("advancements.adventure.root.title"),
                        Text.translatable("advancements.adventure.root.description"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
                .criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer())
                .build(exporter, "adventure/root");

        AdvancementEntry advancementEntry4 = createKillMobAdvancements(advancementEntry, exporter, MONSTERS);

        //endregion

        // region Husbandry

        AdvancementEntry hadvancementEntry = Advancement.Builder.create()
                .display(
                        Blocks.HAY_BLOCK,
                        Text.translatable("advancements.husbandry.root.title"),
                        Text.translatable("advancements.husbandry.root.description"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/husbandry.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("consumed_item", ConsumeItemCriterion.Conditions.any())
                .build(exporter, "husbandry/root");

        AdvancementEntry hadvancementEntry3 = Advancement.Builder.create()
                .parent(hadvancementEntry)
                .display(
                        Items.WHEAT,
                        Text.translatable("advancements.husbandry.breed_an_animal.title"),
                        Text.translatable("advancements.husbandry.breed_an_animal.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .criterion("bred", BredAnimalsCriterion.Conditions.any())
                .build(exporter, "husbandry/breed_an_animal");

        createBreedAllAnimalsAdvancement(hadvancementEntry3, exporter, BREEDABLE_ANIMALS.stream(), EGG_LAYING_ANIMALS.stream());

        AdvancementEntry hadvancementEntry2 = Advancement.Builder.create()
                .parent(hadvancementEntry)
                .display(
                        Items.WHEAT,
                        Text.translatable("advancements.husbandry.plant_seed.title"),
                        Text.translatable("advancements.husbandry.plant_seed.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .criterion("wheat", ItemCriterion.Conditions.createPlacedBlock(Blocks.WHEAT))
                .criterion("pumpkin_stem", ItemCriterion.Conditions.createPlacedBlock(Blocks.PUMPKIN_STEM))
                .criterion("melon_stem", ItemCriterion.Conditions.createPlacedBlock(Blocks.MELON_STEM))
                .criterion("beetroots", ItemCriterion.Conditions.createPlacedBlock(Blocks.BEETROOTS))
                .criterion("nether_wart", ItemCriterion.Conditions.createPlacedBlock(Blocks.NETHER_WART))
                .criterion("torchflower", ItemCriterion.Conditions.createPlacedBlock(Blocks.TORCHFLOWER_CROP))
                .criterion("pitcher_pod", ItemCriterion.Conditions.createPlacedBlock(Blocks.PITCHER_CROP))
                .criterion("super_flower", ItemCriterion.Conditions.createPlacedBlock(ModBlocks.SUPER_FLOWER_CROP))
                .build(exporter, "husbandry/plant_seed");

        requireFoodItemsEaten(Advancement.Builder.create())
                .parent(hadvancementEntry2)
                .display(
                        Items.APPLE,
                        Text.translatable("advancements.husbandry.balanced_diet.title"),
                        Text.translatable("advancements.husbandry.balanced_diet.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "husbandry/balanced_diet");

        // endregion

        // region Nether

        AdvancementEntry nadvancementEntry = Advancement.Builder.create()
                .display(
                        Blocks.RED_NETHER_BRICKS,
                        Text.translatable("advancements.nether.root.title"),
                        Text.translatable("advancements.nether.root.description"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/nether.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(World.NETHER))
                .build(exporter, "nether/root");

        AdvancementEntry nadvancementEntry10 = Advancement.Builder.create()
                .parent(nadvancementEntry)
                .display(
                        Items.ANCIENT_DEBRIS,
                        Text.translatable("advancements.nether.obtain_ancient_debris.title"),
                        Text.translatable("advancements.nether.obtain_ancient_debris.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("ancient_debris", InventoryChangedCriterion.Conditions.items(Items.ANCIENT_DEBRIS))
                .build(exporter, "nether/obtain_ancient_debris");

        AdvancementEntry nadvancementEntry11 = Advancement.Builder.create()
                .parent(nadvancementEntry10)
                .display(
                        Items.NETHERITE_CHESTPLATE,
                        Text.translatable("advancements.nether.netherite_armor.title"),
                        Text.translatable("advancements.nether.netherite_armor.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .criterion(
                        "netherite_armor",
                        InventoryChangedCriterion.Conditions.items(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
                )
                .build(exporter, "nether/netherite_armor");

        AdvancementEntry nadvancementEntry12 = Advancement.Builder.create()
                .parent(nadvancementEntry11)
                .display(
                        ModItems.TITANIUM_CHESTPLATE,
                        Text.translatable("advancements.silvkingsmod.nether.titanium_armor.title"),
                        Text.translatable("advancements.silvkingsmod.nether.titanium_armor.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(150))
                .criterion(
                        "titanium_armor",
                        InventoryChangedCriterion.Conditions.items(ModItems.TITANIUM_HELMET, ModItems.TITANIUM_CHESTPLATE, ModItems.TITANIUM_LEGGINGS, ModItems.TITANIUM_BOOTS)
                )
                .build(exporter, "nether/titanium_armor");

        AdvancementEntry nadvancementEntry3 = Advancement.Builder.create()
                .parent(nadvancementEntry)
                .display(
                        Blocks.NETHER_BRICKS,
                        Text.translatable("advancements.nether.find_fortress.title"),
                        Text.translatable("advancements.nether.find_fortress.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion(
                        "fortress",
                        TickCriterion.Conditions.createLocation(
                                LocationPredicate.Builder.createStructure(wrapperLookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(StructureKeys.FORTRESS))
                        )
                )
                .build(exporter, "nether/find_fortress");

        AdvancementEntry nadvancementEntry6 = Advancement.Builder.create()
                .parent(nadvancementEntry3)
                .display(
                        Items.BLAZE_ROD,
                        Text.translatable("advancements.nether.obtain_blaze_rod.title"),
                        Text.translatable("advancements.nether.obtain_blaze_rod.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("blaze_rod", InventoryChangedCriterion.Conditions.items(Items.BLAZE_ROD))
                .build(exporter, "nether/obtain_blaze_rod");

        AdvancementEntry nadvancementEntry8 = Advancement.Builder.create()
                .parent(nadvancementEntry6)
                .display(
                        Items.POTION,
                        Text.translatable("advancements.nether.brew_potion.title"),
                        Text.translatable("advancements.nether.brew_potion.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("potion", BrewedPotionCriterion.Conditions.any())
                .build(exporter, "nether/brew_potion");

        AdvancementEntry nadvancementEntry9 = Advancement.Builder.create()
                .parent(nadvancementEntry8)
                .display(
                        Items.MILK_BUCKET,
                        Text.translatable("advancements.nether.all_potions.title"),
                        Text.translatable("advancements.nether.all_potions.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .criterion(
                        "all_effects",
                        EffectsChangedCriterion.Conditions.create(
                                EntityEffectPredicate.Builder.create()
                                        .addEffect(StatusEffects.SPEED)
                                        .addEffect(StatusEffects.SLOWNESS)
                                        .addEffect(StatusEffects.STRENGTH)
                                        .addEffect(StatusEffects.JUMP_BOOST)
                                        .addEffect(StatusEffects.REGENERATION)
                                        .addEffect(StatusEffects.FIRE_RESISTANCE)
                                        .addEffect(StatusEffects.WATER_BREATHING)
                                        .addEffect(StatusEffects.INVISIBILITY)
                                        .addEffect(StatusEffects.NIGHT_VISION)
                                        .addEffect(StatusEffects.WEAKNESS)
                                        .addEffect(StatusEffects.POISON)
                                        .addEffect(StatusEffects.SLOW_FALLING)
                                        .addEffect(StatusEffects.RESISTANCE)
                                        .addEffect(StatusEffects.OOZING)
                                        .addEffect(StatusEffects.INFESTED)
                                        .addEffect(StatusEffects.WIND_CHARGED)
                                        .addEffect(StatusEffects.WEAVING)
                                        .addEffect(ModEffects.SLIMEY)
                                        .addEffect(ModEffects.VULNERABILITY)
                        )
                )
                .build(exporter, "nether/all_potions");

        Advancement.Builder.create()
                .parent(nadvancementEntry9)
                .display(
                        Items.BUCKET,
                        Text.translatable("advancements.nether.all_effects.title"),
                        Text.translatable("advancements.nether.all_effects.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        true
                )
                .rewards(AdvancementRewards.Builder.experience(1000))
                .criterion(
                        "all_effects",
                        EffectsChangedCriterion.Conditions.create(
                                EntityEffectPredicate.Builder.create()
                                        .addEffect(StatusEffects.SPEED)
                                        .addEffect(StatusEffects.SLOWNESS)
                                        .addEffect(StatusEffects.STRENGTH)
                                        .addEffect(StatusEffects.JUMP_BOOST)
                                        .addEffect(StatusEffects.REGENERATION)
                                        .addEffect(StatusEffects.FIRE_RESISTANCE)
                                        .addEffect(StatusEffects.WATER_BREATHING)
                                        .addEffect(StatusEffects.INVISIBILITY)
                                        .addEffect(StatusEffects.NIGHT_VISION)
                                        .addEffect(StatusEffects.WEAKNESS)
                                        .addEffect(StatusEffects.POISON)
                                        .addEffect(StatusEffects.WITHER)
                                        .addEffect(StatusEffects.HASTE)
                                        .addEffect(StatusEffects.MINING_FATIGUE)
                                        .addEffect(StatusEffects.LEVITATION)
                                        .addEffect(StatusEffects.GLOWING)
                                        .addEffect(StatusEffects.ABSORPTION)
                                        .addEffect(StatusEffects.HUNGER)
                                        .addEffect(StatusEffects.NAUSEA)
                                        .addEffect(StatusEffects.RESISTANCE)
                                        .addEffect(StatusEffects.SLOW_FALLING)
                                        .addEffect(StatusEffects.CONDUIT_POWER)
                                        .addEffect(StatusEffects.DOLPHINS_GRACE)
                                        .addEffect(StatusEffects.BLINDNESS)
                                        .addEffect(StatusEffects.BAD_OMEN)
                                        .addEffect(StatusEffects.HERO_OF_THE_VILLAGE)
                                        .addEffect(StatusEffects.DARKNESS)
                                        .addEffect(StatusEffects.OOZING)
                                        .addEffect(StatusEffects.INFESTED)
                                        .addEffect(StatusEffects.WIND_CHARGED)
                                        .addEffect(StatusEffects.WEAVING)
                                        .addEffect(StatusEffects.TRIAL_OMEN)
                                        .addEffect(StatusEffects.RAID_OMEN)
                                        .addEffect(ModEffects.SLIMEY)
                                        .addEffect(ModEffects.VULNERABILITY)
                                        .addEffect(ModEffects.ANXIETY)
                        )
                )
                .build(exporter, "nether/all_effects");

        AdvancementEntry nadvancementEntry4 = Advancement.Builder.create()
                .parent(nadvancementEntry3)
                .display(
                        Blocks.WITHER_SKELETON_SKULL,
                        Text.translatable("advancements.nether.get_wither_skull.title"),
                        Text.translatable("advancements.nether.get_wither_skull.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("wither_skull", InventoryChangedCriterion.Conditions.items(Blocks.WITHER_SKELETON_SKULL))
                .build(exporter, "nether/get_wither_skull");

        AdvancementEntry nadvancementEntry5 = Advancement.Builder.create()
                .parent(nadvancementEntry4)
                .display(
                        Items.NETHER_STAR,
                        Text.translatable("advancements.nether.summon_wither.title"),
                        Text.translatable("advancements.nether.summon_wither.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("summoned", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.WITHER)))
                .build(exporter, "nether/summon_wither");

        AdvancementEntry nadvancementEntry13 = Advancement.Builder.create()
                .parent(nadvancementEntry5)
                .display(
                        ModItems.TITAN_HEART,
                        Text.translatable("advancements.silvkingsmod.nether.titan_heart.title"),
                        Text.translatable("advancements.silvkingsmod.nether.titan_heart.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("titan_heart", InventoryChangedCriterion.Conditions.items(ModItems.TITAN_HEART))
                .build(exporter, "nether/titan_heart");

        AdvancementEntry nadvancementEntry7 = Advancement.Builder.create()
                .parent(nadvancementEntry13)
                .display(
                        ModItems.DARK_WORLD_KEY,
                        Text.translatable("advancements.silvkingsmod.nether.dark_key.title"),
                        Text.translatable("advancements.silvkingsmod.nether.dark_key.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_key", InventoryChangedCriterion.Conditions.items(ModItems.DARK_WORLD_KEY))
                .build(exporter, "nether/dark_key");

        AdvancementEntry nadvancementEntry14 = Advancement.Builder.create()
                .parent(nadvancementEntry7)
                .display(
                        ModBlocks.PEDESTAL,
                        Text.translatable("advancements.silvkingsmod.nether.pedestal.title"),
                        Text.translatable("advancements.silvkingsmod.nether.pedestal.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("activated_pedestal", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())) // Trigger in PedestalBlockEntity
                .build(exporter, "nether/pedestal");

        // endregion

        // region End

        AdvancementEntry eadvancementEntry = Advancement.Builder.create()
                .display(
                        Blocks.END_STONE,
                        Text.translatable("advancements.end.root.title"),
                        Text.translatable("advancements.end.root.description"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/end.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("entered_end", ChangedDimensionCriterion.Conditions.to(World.END))
                .build(exporter, "end/root");

        AdvancementEntry eadvancementEntry2 = Advancement.Builder.create()
                .parent(eadvancementEntry)
                .display(
                        Blocks.DRAGON_HEAD,
                        Text.translatable("advancements.end.kill_dragon.title"),
                        Text.translatable("advancements.end.kill_dragon.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("killed_dragon", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON)))
                .build(exporter, "end/kill_dragon");

        AdvancementEntry eadvancementEntry3 = Advancement.Builder.create()
                .parent(eadvancementEntry2)
                .display(
                        Items.ENDER_PEARL,
                        Text.translatable("advancements.end.enter_end_gateway.title"),
                        Text.translatable("advancements.end.enter_end_gateway.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("entered_end_gateway", EnterBlockCriterion.Conditions.block(Blocks.END_GATEWAY))
                .build(exporter, "end/enter_end_gateway");

        AdvancementEntry eadvancementEntry4 = Advancement.Builder.create()
                .parent(eadvancementEntry3)
                .display(
                        Blocks.PURPUR_BLOCK,
                        Text.translatable("advancements.end.find_end_city.title"),
                        Text.translatable("advancements.end.find_end_city.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion(
                        "in_city",
                        TickCriterion.Conditions.createLocation(
                                LocationPredicate.Builder.createStructure(wrapperLookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(StructureKeys.END_CITY))
                        )
                )
                .build(exporter, "end/find_end_city");

        AdvancementEntry eadvancementEntry5 = Advancement.Builder.create()
                .parent(eadvancementEntry4)
                .display(
                        ModBlocks.ETERNAL_BRICKS,
                        Text.translatable("advancements.silvkingsmod.end.find_eternal_city.title"),
                        Text.translatable("advancements.silvkingsmod.end.find_eternal_city.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion(
                        "in_city",
                        TickCriterion.Conditions.createLocation(
                                LocationPredicate.Builder.createStructure(wrapperLookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(ModStructures.ETERNAL_CITY_KEY))
                        )
                )
                .build(exporter, "end/find_eternal_city");

        Advancement.Builder.create()
                .parent(eadvancementEntry5)
                .display(
                        ModItems.ETERNAL_ELYTRA,
                        Text.translatable("advancements.silvkingsmod.end.elytra.title"),
                        Text.translatable("advancements.silvkingsmod.end.elytra.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false
                )
                .criterion("elytra", InventoryChangedCriterion.Conditions.items(ModItems.ETERNAL_ELYTRA))
                .build(exporter, "end/eternal_elytra");

        // endregion

        // region Dark World

        AdvancementEntry modAdvancementEntry = Advancement.Builder.create()
                .display(
                        ModBlocks.DARK_WORLD_PORTAL,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.description"),
                        Identifier.of(SilvKingsMod.MOD_ID,"textures/gui/advancements/backgrounds/dark_world.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("entered_dark_world", ChangedDimensionCriterion.Conditions.to(ModDimensions.DARK_WORLD_KEY))
                .build(exporter, "silvkingsmod:dark_world/root");

        AdvancementEntry modAdvancementEntry2 = Advancement.Builder.create()
                .parent(modAdvancementEntry)
                .display(
                        ModItems.DARK_SHARD_TIER1,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier1.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier1.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_shard_tier1", InventoryChangedCriterion.Conditions.items(ModItems.DARK_SHARD_TIER1))
                .build(exporter, "silvkingsmod:dark_world/obtain_dark_shard_tier1");

        AdvancementEntry modAdvancementEntry3 = Advancement.Builder.create()
                .parent(modAdvancementEntry2)
                .display(
                        ModItems.DARK_SHARD_TIER2,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier2.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier2.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_shard_tier2", InventoryChangedCriterion.Conditions.items(ModItems.DARK_SHARD_TIER2))
                .build(exporter, "silvkingsmod:dark_world/obtain_dark_shard_tier2");

        AdvancementEntry modAdvancementEntry4 = Advancement.Builder.create()
                .parent(modAdvancementEntry3)
                .display(
                        ModItems.DARK_SHARD_TIER3,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier3.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier3.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_shard_tier3", InventoryChangedCriterion.Conditions.items(ModItems.DARK_SHARD_TIER3))
                .build(exporter, "silvkingsmod:dark_world/obtain_dark_shard_tier3");

        AdvancementEntry modAdvancementEntry5 = Advancement.Builder.create()
                .parent(modAdvancementEntry4)
                .display(
                        ModItems.DARK_SHARD_TIER4,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier4.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier4.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_shard_tier4", InventoryChangedCriterion.Conditions.items(ModItems.DARK_SHARD_TIER4))
                .build(exporter, "silvkingsmod:dark_world/obtain_dark_shard_tier4");

        AdvancementEntry modAdvancementEntry6 = Advancement.Builder.create()
                .parent(modAdvancementEntry5)
                .display(
                        ModItems.DARK_SHARD_TIER5,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier5.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_shard_tier5.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_shard_tier5", InventoryChangedCriterion.Conditions.items(ModItems.DARK_SHARD_TIER5))
                .build(exporter, "silvkingsmod:dark_world/obtain_dark_shard_tier5");

        AdvancementEntry modAdvancementEntry7 = Advancement.Builder.create()
                .parent(modAdvancementEntry)
                .display(
                        ModItems.DARK_TITANIUM_INGOT,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.find_dark_bastion.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.find_dark_bastion.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_titanium", InventoryChangedCriterion.Conditions.items(ModItems.DARK_TITANIUM_INGOT))
                .build(exporter, "silvkingsmod:dark_world/find_dark_bastion");

        AdvancementEntry modAdvancementEntry10 = Advancement.Builder.create()
                .parent(modAdvancementEntry7)
                .display(
                        ModItems.DARK_TITANIUM_CHESTPLATE,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_armor.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.obtain_dark_armor.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(200))
                .criterion(
                        "dark_titanium_armor",
                        InventoryChangedCriterion.Conditions.items(ModItems.DARK_TITANIUM_HELMET, ModItems.DARK_TITANIUM_CHESTPLATE, ModItems.DARK_TITANIUM_LEGGINGS, ModItems.DARK_TITANIUM_BOOTS)
                )
                .build(exporter, "silvkingsmod:dark_world/obtain_dark_armor");

        AdvancementEntry modAdvancementEntry8 = Advancement.Builder.create()
                .parent(modAdvancementEntry)
                .display(
                        ModItems.TITANIUM_SWORD,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.beyond_borders.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.beyond_borders.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("far_from_spawn", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())) // Trigger in DarkWorldHandler
                .build(exporter, "silvkingsmod:dark_world/beyond_borders");

        AdvancementEntry modAdvancementEntry9 = Advancement.Builder.create()
                .parent(modAdvancementEntry8)
                .display(
                        ModItems.DARK_TITANIUM_SWORD,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.world_edge.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.world_edge.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("further_from_spawn", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())) // Trigger in DarkWorldHandler
                .build(exporter, "silvkingsmod:dark_world/world_edge");

        AdvancementEntry modAdvancementEntry11 = Advancement.Builder.create()
                .parent(modAdvancementEntry)
                .display(
                        ModItems.DARK_CORE_TIER1,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.dark_core.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.dark_core.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_core", InventoryChangedCriterion.Conditions.items(ModItems.DARK_CORE_TIER1))
                .build(exporter, "silvkingsmod:dark_world/dark_core");

        AdvancementEntry modAdvancementEntry12 = Advancement.Builder.create()
                .parent(modAdvancementEntry11)
                .display(
                        ModItems.DARK_CORE_TIER5,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.dark_core5.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.dark_core5.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(150))
                .criterion("dark_core", InventoryChangedCriterion.Conditions.items(ModItems.DARK_CORE_TIER5))
                .build(exporter, "silvkingsmod:dark_world/dark_core5");

        AdvancementEntry modAdvancementEntry13 = Advancement.Builder.create()
                .parent(modAdvancementEntry11)
                .display(
                        ModBlocks.CORE_REFINERY,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.refinery.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.refinery.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("has_item_1", InventoryChangedCriterion.Conditions.items(ModItems.REFINED_WEAPON_CORE))
                .criterion("has_item_2", InventoryChangedCriterion.Conditions.items(ModItems.REFINED_ARMOR_CORE))
                .criterion("has_item_3", InventoryChangedCriterion.Conditions.items(ModItems.REFINED_TOOL_CORE))
                .requirements(AdvancementRequirements.anyOf(List.of("has_item_1", "has_item_2", "has_item_3")))
                .build(exporter, "silvkingsmod:dark_world/refinery");

        AdvancementEntry modAdvancementEntry14 = Advancement.Builder.create()
                .parent(modAdvancementEntry13)
                .display(
                        ModBlocks.DARK_ANVIL,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.dark_anvil.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.dark_anvil.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("dark_anvil", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())) // Trigger in DarkAnvilScreenHandler
                .build(exporter, "silvkingsmod:dark_world/dark_anvil");

        ItemStack enchantedDarkSword = new ItemStack(ModItems.DARK_TITANIUM_SWORD);
        enchantedDarkSword.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        AdvancementEntry modAdvancementEntry15 = Advancement.Builder.create()
                .parent(modAdvancementEntry9)
                .display(
                        enchantedDarkSword,
                        Text.translatable("advancements.silvkingsmod.dark_world.root.max_world_edge.title"),
                        Text.translatable("advancements.silvkingsmod.dark_world.root.max_world_edge.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(150))
                .criterion("furthest_from_spawn", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())) // Trigger in DarkWorldHandler
                .build(exporter, "silvkingsmod:dark_world/max_world_edge");
        // endregion
    }

    public static AdvancementEntry createKillMobAdvancements(AdvancementEntry parent, Consumer<AdvancementEntry> exporter, List<EntityType<?>> monsters) {
        AdvancementEntry advancementEntry = requireListedMobsKilled(Advancement.Builder.create(), monsters)
                .parent(parent)
                .display(
                        Items.IRON_SWORD,
                        Text.translatable("advancements.adventure.kill_a_mob.title"),
                        Text.translatable("advancements.adventure.kill_a_mob.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .build(exporter, "adventure/kill_a_mob");

        requireListedMobsKilled(Advancement.Builder.create(), monsters)
                .parent(advancementEntry)
                .display(
                        Items.DIAMOND_SWORD,
                        Text.translatable("advancements.adventure.kill_all_mobs.title"),
                        Text.translatable("advancements.adventure.kill_all_mobs.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "adventure/kill_all_mobs");


        return advancementEntry;
    }

    public static AdvancementEntry createBreedAllAnimalsAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> exporter, Stream<EntityType<?>> breedableAnimals, Stream<EntityType<?>> eggLayingAnimals) {
        return requireListedAnimalsBred(Advancement.Builder.create(), breedableAnimals, eggLayingAnimals)
                .parent(parent)
                .display(
                        Items.GOLDEN_CARROT,
                        Text.translatable("advancements.husbandry.breed_all_animals.title"),
                        Text.translatable("advancements.husbandry.breed_all_animals.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "husbandry/bred_all_animals");
    }

    private static Advancement.Builder requireListedAnimalsBred(Advancement.Builder advancementBuilder, Stream<EntityType<?>> breedableAnimals, Stream<EntityType<?>> eggLayingAnimals) {
        breedableAnimals.forEach(
                type -> advancementBuilder.criterion(EntityType.getId(type).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(type)))
        );
        eggLayingAnimals.forEach(
                type -> advancementBuilder.criterion(
                        EntityType.getId(type).toString(),
                        BredAnimalsCriterion.Conditions.create(
                                Optional.of(EntityPredicate.Builder.create().type(type).build()), Optional.of(EntityPredicate.Builder.create().type(type).build()), Optional.empty()
                        )
                )
        );
        return advancementBuilder;
    }

    private static Advancement.Builder requireListedMobsKilled(Advancement.Builder builder, List<EntityType<?>> entityTypes) {
        entityTypes.forEach(
                type -> builder.criterion(
                        Registries.ENTITY_TYPE.getId(type).toString(), OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(type))
                )
        );
        return builder;
    }

    private static Advancement.Builder requireFoodItemsEaten(Advancement.Builder builder) {
        for (Item item : FOOD_ITEMS) {
            builder.criterion(Registries.ITEM.getId(item).getPath(), ConsumeItemCriterion.Conditions.item(item));
        }

        return builder;
    }
}
