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
            EntityType.Builder.create(MantisEntity::new, SpawnGroup.CREATURE)
                    .dimensions(2f,2.5f)
                    .build());

    public static final EntityType<TitanPlayerEntity> TITAN_PLAYER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "titan_player"),
            EntityType.Builder.create(TitanPlayerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.5f,1.75f)
                    .build());

    public static final EntityType<MagnaTitanEntity> MAGNA_TITAN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_titan"),
            EntityType.Builder.create(MagnaTitanEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.8f,2.75f)
                    .makeFireImmune()
                    .build());

    public static final EntityType<MagnaMinionEntity> MAGNA_MINION = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_minion"),
            EntityType.Builder.create(MagnaMinionEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.5f,1.5f)
                    .makeFireImmune()
                    .build());

    public static final EntityType<TomahawkProjectileEntity> TOMAHAWK = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "tomahawk"),
            EntityType.Builder.<TomahawkProjectileEntity>create(TomahawkProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f,1.15f)
                    .build());

    public static final EntityType<ChairEntity> CHAIR_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "chair_entity"),
            EntityType.Builder.create(ChairEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f,0.5f)
                    .disableSummon()
                    .build());

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
                    .makeFireImmune()
                    .dimensions(1.4f, 2.7f)
                    .build());

    public static final EntityType<MagnaWitchEntity> MAGNA_WITCH = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_witch"),
            EntityType.Builder.create(MagnaWitchEntity::new, SpawnGroup.MONSTER)
                    .makeFireImmune()
                    .dimensions(0.6f, 1.95f)
                    .build());

    public static final EntityType<MagnaFireballEntity> MAGNA_FIREBALL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_fireball"),
            EntityType.Builder.<MagnaFireballEntity>create(MagnaFireballEntity::new, SpawnGroup.MISC)
                    .dimensions(1.2f, 1.2f)
                    .build());

    public static final EntityType<MagnaAnvilEntity> MAGNA_ANVIL = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_anvil"),
            EntityType.Builder.<MagnaAnvilEntity>create(MagnaAnvilEntity::new, SpawnGroup.MISC)
                    .dimensions(4.0f, 2.2f)
                    .build());

    public static final EntityType<MagnaBombEntity> MAGNA_BOMB = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "magna_bomb"),
            EntityType.Builder.create(MagnaBombEntity::new, SpawnGroup.MISC)
                    .dimensions(2.5f, 2.5f)
                    .build());

    public static final EntityType<EternalShulkerEntity> ETERNAL_SHULKER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "eternal_shulker"),
            EntityType.Builder.create(EternalShulkerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.0f, 1.0f)
                    .build());

    public static final EntityType<EternalBulletEntity> ETERNAL_BULLET = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "eternal_bullet"),
            EntityType.Builder.<EternalBulletEntity>create(EternalBulletEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.3125f, 0.2f)
                    .build());

    public static final EntityType<DarkShadowEntity> DARK_SHADOW = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "dark_shadow"),
            EntityType.Builder.create(DarkShadowEntity::new, SpawnGroup.MONSTER)
                    .makeFireImmune()
                    .dimensions(0.7f, 0.7f)
                    .build());

    public static final EntityType<NecroPigEntity> NECRO_PIG = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necro_pig"),
            EntityType.Builder.create(NecroPigEntity::new, SpawnGroup.MONSTER)
                    .makeFireImmune()
                    .dimensions(0.9f, 0.9f)
                    .build());

    public static final EntityType<NecroCowEntity> NECRO_COW = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necro_cow"),
            EntityType.Builder.create(NecroCowEntity::new, SpawnGroup.MONSTER)
                    .makeFireImmune()
                    .dimensions(0.9f, 1.4f)
                    .build());

    public static final EntityType<NecroChickenEntity> NECRO_CHICKEN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID,  "necro_chicken"),
            EntityType.Builder.create(NecroChickenEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.2f, 2.1f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<NecroMiniChickenEntity> NECRO_MINI_CHICKEN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID,  "necro_mini_chicken"),
            EntityType.Builder.create(NecroMiniChickenEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.35f, 0.58f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<NecroSheepEntity> NECRO_SHEEP = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necro_sheep"),
            EntityType.Builder.create(NecroSheepEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.9f, 1.3f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<NecroBeeEntity> NECRO_BEE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necro_bee"),
            EntityType.Builder.create(NecroBeeEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.7f, 0.6f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<AbyssalShadowEntity> ABYSSAL_SHADOW = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "abyssal_shadow"),
            EntityType.Builder.create(AbyssalShadowEntity::new, SpawnGroup.MONSTER)
                    .makeFireImmune()
                    .dimensions(0.7f, 0.7f)
                    .build());

    public static final EntityType<NecroWolfEntity> NECRO_WOLF = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necro_wolf"),
            EntityType.Builder.create(NecroWolfEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 0.85f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<PullEntity> PULL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "pull"),
            EntityType.Builder.create(PullEntity::new, SpawnGroup.MISC)
                    .dimensions(0.1f, 0.1f)
                    .build()
    );

    public static final EntityType<TornadoEntity> TORNADO = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "tornado"),
            EntityType.Builder.create(TornadoEntity::new, SpawnGroup.MISC)
                    .dimensions(0.1f, 0.1f)
                    .build()
    );

    public static final EntityType<NecroLlamaEntity> NECRO_LLAMA = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necro_llama"),
            EntityType.Builder.create(NecroLlamaEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.9f, 1.87f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<GeckoEntity> GECKO = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "gecko"),
            EntityType.Builder.create(GeckoEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.4f, 0.35f)
                    .build()
    );

    public static final EntityType<RhinoEntity> RHINO = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "rhino"),
            EntityType.Builder.create(RhinoEntity::new, SpawnGroup.CREATURE)
                    .dimensions(2.5f, 2.5f)
                    .build()
    );

    public static final EntityType<NecroGeckoEntity> NECRO_GECKO = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necro_gecko"),
            EntityType.Builder.create(NecroGeckoEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.2f, 1.05f)
                    .makeFireImmune()
                    .build()
    );

    public static final EntityType<NecrosaurusEntity> NECROSAURUS = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SilvKingsMod.MOD_ID, "necrosaurus"),
            EntityType.Builder.create(NecrosaurusEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.2f, 1.05f)
                    .makeFireImmune()
                    .build()
    );


    public static void registerModEntities() {
        SilvKingsMod.LOGGER.info("Registering Mod Entities for " + SilvKingsMod.MOD_ID);
    }
}
