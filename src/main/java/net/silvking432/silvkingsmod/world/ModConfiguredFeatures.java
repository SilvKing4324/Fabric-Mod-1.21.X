package net.silvking432.silvkingsmod.world;

import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.silvking432.silvkingsmod.block.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> TITANIUM_ORE_KEY = registerKey("titanium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> TITANIUM_NETHER_ORE_KEY = registerKey("titanium_nether_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> TITANIUM_END_ORE_KEY = registerKey("titanium_end_ore");


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

    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(SilvKingsMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}