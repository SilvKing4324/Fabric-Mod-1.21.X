package net.silvking432.silvkingsmod.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.entity.custom.GrowthChamberBlockEntity;
import net.silvking432.silvkingsmod.block.entity.custom.PedestalBlockEntity;
import net.silvking432.silvkingsmod.block.entity.custom.PulseEmitterBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(SilvKingsMod.MOD_ID, "pedestal_be"),
                    BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build(null));

    public static final BlockEntityType<GrowthChamberBlockEntity> GROWTH_CHAMBER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(SilvKingsMod.MOD_ID, "growth_chamber_be"),
                    BlockEntityType.Builder.create(GrowthChamberBlockEntity::new, ModBlocks.GROWTH_CHAMBER).build(null));

    public static final BlockEntityType<PulseEmitterBlockEntity> PULSE_EMITTER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(SilvKingsMod.MOD_ID, "pulse_emitter_be"),
                    BlockEntityType.Builder.create(PulseEmitterBlockEntity::new, ModBlocks.PULSE_EMITTER).build(null));

    public static void registerBlockEntities() {
        SilvKingsMod.LOGGER.info("Registering block Entities for " + SilvKingsMod.MOD_ID);
    }
}
