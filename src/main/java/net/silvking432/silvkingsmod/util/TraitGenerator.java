package net.silvking432.silvkingsmod.util;

import net.minecraft.item.ItemStack;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;

import java.util.*;

public class TraitGenerator {
    public enum TraitApplication{
        ARMOR,
        WEAPON,
        TOOL
    }

    public enum TraitType {
        // Armor
        DARK_FOG_RES("Dark Fog Resistance", "%", false, TraitApplication.ARMOR, 2f, 5f, 4f, 8f, 6f, 10f),
        EXTRA_HEALTH("Extra Health", " HP", false, TraitApplication.ARMOR, 1f, 3f, 2f, 4f, 3f, 5f),
        EXTRA_VISION("Dark Fog Extra Vision", " Blocks", false, TraitApplication.ARMOR, 1f, 5f, 3f, 9f, 5f, 12f),
        BONUS_SPEED("Speed", "%", false, TraitApplication.ARMOR, 2f, 5f, 4f, 8f, 6f, 10f),
        DAMAGE_REDUCTION("Damage Reduction", "%", false, TraitApplication.ARMOR, 1f, 5f, 2f, 6f, 3f, 7.5f),
        EFFECT_DURATION("Positive Effects Duration", "s", false, TraitApplication.ARMOR, 3f, 10f, 5f, 15f, 7f, 20f),

        RARE_FINDER("§cRare Creature Finder", "%", true, TraitApplication.ARMOR, 0f, 0f, 0f, 0f, 10f, 10f),

        // Weapon
        EXTRA_DAMAGE("Extra Damage", "%", false, TraitApplication.WEAPON, 2f, 10f, 4f, 20f, 6f, 30f),
        EXTRA_CRIT_BONUS("Extra Crit Bonus", "%", false, TraitApplication.WEAPON, 2f, 10f, 4f, 20f, 6f, 30f),
        EXTRA_ATTACK_SPEED("Extra Attack Speed", "%", false, TraitApplication.WEAPON, 3f, 5f, 6f, 10f, 9f, 15f),

        BONUS_SHARDS("§cBonus Shards", "%", true, TraitApplication.WEAPON, 0f, 0f, 0f, 0f, 15f, 30f),

        // Tool
        EXTRA_DURABILITY("Extra Durability", " Points", false, TraitApplication.TOOL, 1f, 100f, 50f, 200f, 100f, 300f),
        FAST_MINER("Fast Miner", "%", false, TraitApplication.TOOL, 1f, 5f, 3f, 10f, 6f, 20f),
        BONUS_DROPS("Bonus Drops", "%", false, TraitApplication.TOOL, 1f, 5f, 3f, 10f, 6f, 20f),

        RANGE_EXTEND("§cRange", " Block", true, TraitApplication.TOOL, 0f, 0f, 0f, 0f, 1f, 1f);

        public final String displayName, unit;
        public final boolean isSpecial;
        public final TraitApplication type;
        public final float t1Min, t1Max, t2Min, t2Max, t3Min, t3Max;

        TraitType(String displayName, String unit, boolean isSpecial, TraitApplication type, float t1Min, float t1Max, float t2Min, float t2Max, float t3Min, float t3Max) {
            this.displayName = displayName;
            this.unit = unit;
            this.isSpecial = isSpecial;
            this.type = type;
            this.t1Min = t1Min; this.t1Max = t1Max;
            this.t2Min = t2Min; this.t2Max = t2Max;
            this.t3Min = t3Min; this.t3Max = t3Max;
        }

        public float rollValue(int traitTier, Random random) {
            float min = t1Min, max = t1Max;
            if (traitTier == 2) { min = t2Min; max = t2Max; }
            if (traitTier >= 3) { min = t3Min; max = t3Max; }

            float val = min + random.nextFloat() * (max - min);
            return Math.round(val * 10f) / 10f;
        }
    }

    public enum SkillTraitType {
        PEARL_MAYHEM("Pearl Mayhem", " (Pearls deal no damage and have half Cooldown)", TraitApplication.ARMOR),
        UNBREAKING("Unbreaking", " (20% Chance to ignore Item Damage on this Piece)", TraitApplication.ARMOR),

        VOID_DAMAGE("Void Damage", " (Changes Damage Type to Void Damage)", TraitApplication.WEAPON),
        NECRO_SLAYER("Necro Slayer", " (2x Damage vs Necro-Mobs)", TraitApplication.WEAPON),

        BLOCK_MENDER("Block Mender", " (Consumes basic Blocks for durability)", TraitApplication.TOOL),
        STONE_ORE("Stone Ore", " (Basic Blocks drop random ores sometimes)", TraitApplication.TOOL),

        ;
        public final String displayName;
        public final String suffix;
        public final TraitApplication type;

        SkillTraitType(String displayName, String suffix, TraitApplication type) {
            this.displayName = displayName;
            this.suffix = suffix;
            this.type = type;
        }
    }

    public static void applyRandomTraits(ItemStack stack, int itemTier, TraitApplication type) {
        Random random = new Random();
        List<CoreTrait> rolledTraits = new ArrayList<>();

        TraitApplication filterType = switch (type) {
            case WEAPON -> TraitApplication.WEAPON;
            case TOOL -> TraitApplication.TOOL;
            default -> TraitApplication.ARMOR;
        };

        List<TraitType> normalPool = Arrays.stream(TraitType.values()).filter(t -> !t.isSpecial && t.type == filterType).toList();
        List<TraitType> rarePool = Arrays.stream(TraitType.values()).filter(t -> t.isSpecial && t.type == filterType).toList();

        int count = switch (itemTier) {
            case 2, 3 -> 2;
            case 4 -> 3;
            case 5 -> 4;
            default -> 1;
        };

        int traitQuality = (itemTier <= 2) ? 1 : (itemTier <= 4) ? 2 : 3;

        int maxNormalSlots = Math.min(count, 3);
        for (int i = 0; i < maxNormalSlots; i++) {
            TraitType selected;
            if (i == 2) {
                if (random.nextFloat() < 0.3f) {
                    selected = rarePool.get(random.nextInt(rarePool.size()));
                } else {
                    selected = normalPool.get(random.nextInt(normalPool.size()));
                }
            } else {
                selected = normalPool.get(random.nextInt(normalPool.size()));
            }

            // Prevents reroll of same trait
            TraitType finalSelected = selected;
            if (rolledTraits.stream().noneMatch(t -> t.traitId().equals(finalSelected.name()))) {
                rolledTraits.add(new CoreTrait(selected.name(), selected.rollValue(traitQuality, random)));
            } else {
                i--;
            }
        }

        if (itemTier == 5) {
            List<SkillTraitType> applicableSkills = Arrays.stream(SkillTraitType.values())
                    .filter(s -> s.type == filterType)
                    .toList();

            if (!applicableSkills.isEmpty()) {
                SkillTraitType skill = applicableSkills.get(random.nextInt(applicableSkills.size()));
                rolledTraits.add(new CoreTrait("SKILL_" + skill.name(), 1.0f));
            }
        }
        stack.set(ModDataComponentTypes.CORE_TRAITS, rolledTraits);
    }
}