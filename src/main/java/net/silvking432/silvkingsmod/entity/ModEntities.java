package net.silvking432.silvkingsmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.*;

public class ModEntities {

    public static final EntityType<MantisEntity> MANTIS = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "mantis"),
            EntityType.Builder.create(MantisEntity::new, SpawnGroup.CREATURE).dimensions(2f,2.5f).build());

    public static final EntityType<TitanPlayerEntity> TITAN_PLAYER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "titan_player"),
            EntityType.Builder.create(TitanPlayerEntity::new, SpawnGroup.MONSTER).dimensions(0.5f,1.75f).build());

    public static final EntityType<TomahawkProjectileEntity> TOMAHAWK = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "tomahawk"),
            EntityType.Builder.<TomahawkProjectileEntity>create(TomahawkProjectileEntity::new, SpawnGroup.MISC).dimensions(0.5f,1.15f).build());

    public static final EntityType<ChairEntity> CHAIR_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "chair_entity"),
            EntityType.Builder.create(ChairEntity::new, SpawnGroup.MISC).dimensions(0.5f,0.5f).build());

    public static final EntityType<TitaniumTntEntity> TITANIUM_TNT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "titanium_tnt_entity"),
            EntityType.Builder.<TitaniumTntEntity>create(TitaniumTntEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98f, 0.98f)
                    .build("titanium_tnt_entity")
    );



    public static void registerModEntities() {
        SilvKingsMod.LOGGER.info("Registering Mod Entities for " + SilvKingsMod.MOD_ID);
    }
}
