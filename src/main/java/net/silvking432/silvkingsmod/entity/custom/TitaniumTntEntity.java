package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

public class TitaniumTntEntity extends TntEntity {
    public TitaniumTntEntity(EntityType<? extends TntEntity> type, World world) {
        super(type, world);
    }

    public TitaniumTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(ModEntities.TITANIUM_TNT_ENTITY, world);
        this.updatePosition(x, y, z);
        this.setFuse(100);
    }



    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && this.getFuse() == 99) {
            this.getWorld().playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_TNT_PRIMED,
                    SoundCategory.BLOCKS,
                    1.0F,
                    1.0F
            );
        }

        if (!this.getWorld().isClient && this.getFuse() <= 0) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 8.0f, World.ExplosionSourceType.BLOCK);

            this.discard();
        }
    }
}
