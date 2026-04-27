package net.silvking432.silvkingsmod.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.entity.custom.*;

public class ModBlockEntities {
    public static BlockEntityType<PedestalBlockEntity> PEDESTAL_BE;
    public static BlockEntityType<GrowthChamberBlockEntity> GROWTH_CHAMBER_BE;
    public static BlockEntityType<PulseEmitterBlockEntity> PULSE_EMITTER_BE;
    public static BlockEntityType<EternalShulkerBoxBlockEntity> ETERNAL_SHULKER_BOX_BE;
    public static BlockEntityType<CoreRefineryBlockEntity> CORE_REFINERY_BE;

    public static void registerBlockEntities() {
        SilvKingsMod.LOGGER.info("Registering Block Entities for " + SilvKingsMod.MOD_ID);

        PEDESTAL_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SilvKingsMod.MOD_ID, "pedestal_be"),
                BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build(null));

        GROWTH_CHAMBER_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SilvKingsMod.MOD_ID, "growth_chamber_be"),
                BlockEntityType.Builder.create(GrowthChamberBlockEntity::new, ModBlocks.GROWTH_CHAMBER).build(null));

        PULSE_EMITTER_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SilvKingsMod.MOD_ID, "pulse_emitter_be"),
                BlockEntityType.Builder.create(PulseEmitterBlockEntity::new, ModBlocks.PULSE_EMITTER).build(null));

        ETERNAL_SHULKER_BOX_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SilvKingsMod.MOD_ID, "eternal_shulker_box_be"),
                BlockEntityType.Builder.create(EternalShulkerBoxBlockEntity::new, ModBlocks.ETERNAL_SHULKER_BOX).build(null));

        CORE_REFINERY_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SilvKingsMod.MOD_ID, "core_refinery_be"),
                BlockEntityType.Builder.create(CoreRefineryBlockEntity::new, ModBlocks.CORE_REFINERY).build(null));


    }
}