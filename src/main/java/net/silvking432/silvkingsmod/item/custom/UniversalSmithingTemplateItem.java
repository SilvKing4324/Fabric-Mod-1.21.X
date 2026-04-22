package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class UniversalSmithingTemplateItem extends SmithingTemplateItem {
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;

    private static final Text INGREDIENTS_TEXT = Text.literal("Ingredients").formatted(TITLE_FORMATTING);
    private static final Text APPLIES_TO_TEXT = Text.literal("Applies to:").formatted(TITLE_FORMATTING);

    private static final Text UNIVERSAL_UPGRADE_TEXT = Text.literal("Universal Upgrade").formatted(TITLE_FORMATTING);
    private static final Text UNIVERSAL_APPLIES_TO_TEXT = Text.literal("Diamond, Netherite or Titan Equipment").formatted(DESCRIPTION_FORMATTING);
    private static final Text UNIVERSAL_INGREDIENTS_TEXT = Text.literal("Netherite, Titan or §cDark Titan").formatted(DESCRIPTION_FORMATTING);
    private static final Text UNIVERSAL_BASE_SLOT_DESCRIPTION_TEXT = Text.literal("Add armor, weapon, or tool");
    private static final Text UNIVERSAL_ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.literal("Add upgrade material");

    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");
    private static final Identifier EMPTY_SLOT_SWORD_TEXTURE = Identifier.ofVanilla("item/empty_slot_sword");
    private static final Identifier EMPTY_SLOT_PICKAXE_TEXTURE = Identifier.ofVanilla("item/empty_slot_pickaxe");
    private static final Identifier EMPTY_SLOT_AXE_TEXTURE = Identifier.ofVanilla("item/empty_slot_axe");
    private static final Identifier EMPTY_SLOT_HOE_TEXTURE = Identifier.ofVanilla("item/empty_slot_hoe");
    private static final Identifier EMPTY_SLOT_SHOVEL_TEXTURE = Identifier.ofVanilla("item/empty_slot_shovel");
    private static final Identifier EMPTY_SLOT_INGOT_TEXTURE = Identifier.ofVanilla("item/empty_slot_ingot");

    public UniversalSmithingTemplateItem(
            Text appliesToText,
            Text ingredientsText,
            Text titleText,
            Text baseSlotDescriptionText,
            Text additionsSlotDescriptionText,
            List<Identifier> emptyBaseSlotTextures,
            List<Identifier> emptyAdditionsSlotTextures
    ) {
        super(appliesToText, ingredientsText, titleText, baseSlotDescriptionText, additionsSlotDescriptionText, emptyBaseSlotTextures, emptyAdditionsSlotTextures);
    }

    public static UniversalSmithingTemplateItem createUniversalUpgrade() {
        return new UniversalSmithingTemplateItem(
                UNIVERSAL_APPLIES_TO_TEXT,
                UNIVERSAL_INGREDIENTS_TEXT,
                UNIVERSAL_UPGRADE_TEXT,
                UNIVERSAL_BASE_SLOT_DESCRIPTION_TEXT,
                UNIVERSAL_ADDITIONS_SLOT_DESCRIPTION_TEXT,
                getUniversalUpgradeEmptyBaseSlotTextures(),
                getUniversalUpgradeEmptyAdditionsSlotTextures()
        );
    }

    private static List<Identifier> getUniversalUpgradeEmptyBaseSlotTextures() {
        return List.of(
                EMPTY_ARMOR_SLOT_HELMET_TEXTURE,
                EMPTY_SLOT_SWORD_TEXTURE,
                EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE,
                EMPTY_SLOT_PICKAXE_TEXTURE,
                EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE,
                EMPTY_SLOT_AXE_TEXTURE,
                EMPTY_ARMOR_SLOT_BOOTS_TEXTURE,
                EMPTY_SLOT_HOE_TEXTURE,
                EMPTY_SLOT_SHOVEL_TEXTURE
        );
    }

    private static List<Identifier> getUniversalUpgradeEmptyAdditionsSlotTextures() {
        return List.of(EMPTY_SLOT_INGOT_TEXTURE);
    }
}