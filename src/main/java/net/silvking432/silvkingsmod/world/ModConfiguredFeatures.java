package net.silvking432.silvkingsmod.world;

import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.custom.HoneyBerryBushBlock;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_KEY = registerKey("titanium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> TITANIUM_NETHER_ORE_KEY = registerKey("titanium_nether_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> TITANIUM_END_ORE_KEY = registerKey("titanium_end_ore");

    public static final RegistryKey<ConfiguredFeature<?, ?>> DRIFTWOOD_KEY = registerKey("driftwood");
    public static final RegistryKey<ConfiguredFeature<?, ?>> HONEY_BERRY_BUSH_KEY = registerKey("honey_berry_bush");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplaceables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
        RuleTest endReplaceables = new BlockMatchRuleTest(Blocks.END_STONE);

        List<OreFeatureConfig.Target> overworldTitaniumOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.TITANIUM_ORE.getDefaultState()),
                    OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.TITANIUM_DEEPSLATE_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> netherTitaniumOres =
                List.of(OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.TITANIUM_NETHER_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> endTitaniumOres =
                List.of(OreFeatureConfig.createTarget(endReplaceables, ModBlocks.TITANIUM_END_ORE.getDefaultState()));

        register(context, TITANIUM_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldTitaniumOres,4,0.95f));
        register(context, TITANIUM_NETHER_ORE_KEY, Feature.ORE, new OreFeatureConfig(netherTitaniumOres,4,1.0f));
        register(context, TITANIUM_END_ORE_KEY, Feature.ORE, new OreFeatureConfig(endTitaniumOres,5,1.0f));
        register(context, DRIFTWOOD_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.DRIFTWOOD_LOG),
                new StraightTrunkPlacer(5, 6, 3),

                BlockStateProvider.of(ModBlocks.DRIFTWOOD_LEAVES),
                new BlobFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1), 3),

                new TwoLayersFeatureSize(1, 0, 2)).dirtProvider(BlockStateProvider.of(Blocks.STONE)).build());

        register(context, HONEY_BERRY_BUSH_KEY,Feature.RANDOM_PATCH,
                ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.HONEY_BERRY_BUSH.getDefaultState().with(HoneyBerryBushBlock.AGE, 3))),
                        List.of(Blocks.GRASS_BLOCK)));

    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(SilvKingsMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}