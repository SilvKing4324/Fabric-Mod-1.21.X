package net.silvking432.silvkingsmod.trim;

import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.Map;

public class ModTrimMaterials {

    public static final RegistryKey<ArmorTrimMaterial> TITANIUM = RegistryKey.of(RegistryKeys.TRIM_MATERIAL,
            Identifier.of(SilvKingsMod.MOD_ID, "titanium"));

    public static void bootstrap(Registerable<ArmorTrimMaterial> registerable) {
        register(registerable, TITANIUM, Registries.ITEM.getEntry(ModItems.TITANIUM_INGOT),
                Style.EMPTY.withColor(TextColor.parse("#b03fe0").getOrThrow()), 1.0f);
    }

    private static void register(Registerable<ArmorTrimMaterial> registerable, RegistryKey<ArmorTrimMaterial> armorTrimKey,
                                 RegistryEntry<Item> item, Style style, float itemModelIndex) {
        ArmorTrimMaterial trimMaterial = new ArmorTrimMaterial(armorTrimKey.getValue().getPath(), item, itemModelIndex, Map.of(),
                Text.translatable(Util.createTranslationKey("trim_material", armorTrimKey.getValue())).fillStyle(style));

        registerable.register(armorTrimKey, trimMaterial);
    }
}
