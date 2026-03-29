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

    public static final EntityType<MagnaTitanEntity> MAGNA_TITAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_titan"),
            EntityType.Builder.create(MagnaTitanEntity::new, SpawnGroup.MONSTER).dimensions(0.8f,2.75f).build());

    public static final EntityType<MagnaMinionEntity> MAGNA_MINION = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_minion"),
            EntityType.Builder.create(MagnaMinionEntity::new, SpawnGroup.MONSTER).dimensions(0.5f,1.5f).build());

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

    public static final EntityType<LavaGolemEntity> LAVA_GOLEM = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "lava_golem"),
            EntityType.Builder.create(LavaGolemEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.4f, 2.7f)
                    .build());

    public static final EntityType<MagnaWitchEntity> MAGNA_WITCH = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_witch"),
            EntityType.Builder.create(MagnaWitchEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.95f)
                    .build());

    public static final EntityType<MagnaFireballEntity> MAGNA_FIREBALL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_fireball"),
            EntityType.Builder.<MagnaFireballEntity>create(MagnaFireballEntity::new, SpawnGroup.MISC)
                    .dimensions(1.2f, 1.2f) // Größe des Feuerballs
                    .build());

    public static final EntityType<MagnaAnvilEntity> MAGNA_ANVIL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_anvil"),
            EntityType.Builder.<MagnaAnvilEntity>create(MagnaAnvilEntity::new, SpawnGroup.MISC)
                    .dimensions(4.0f, 2.2f) // Größere Hitbox, weil der Amboss ja riesig ist!
                    .build());

    public static final EntityType<MagnaBombEntity> MAGNA_BOMB = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_bomb"),
            EntityType.Builder.<MagnaBombEntity>create(MagnaBombEntity::new, SpawnGroup.MISC)
                    .dimensions(2.5f, 2.5f) // Größere Hitbox, weil der Amboss ja riesig ist!
                    .build());


    public static void registerModEntities() {
        SilvKingsMod.LOGGER.info("Registering Mod Entities for " + SilvKingsMod.MOD_ID);
    }
}
