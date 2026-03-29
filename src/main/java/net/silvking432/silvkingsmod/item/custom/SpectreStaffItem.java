package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.custom.MagnaFireballEntity;

public class SpectreStaffItem extends Item {
    public SpectreStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.2f);

        if (!world.isClient) {
            // 1. Blickrichtung des Spielers holen
            Vec3d lookDir = user.getRotationVec(1.0f);

            // 2. Deine MagnaFireballEntity erstellen (wie beim Boss)
            // Nutzt deinen Konstruktor (World, Owner, Velocity)
            MagnaFireballEntity fireball = new MagnaFireballEntity(world, user, lookDir);

            // 3. Startposition: Etwas vor dem Gesicht des Spielers
            fireball.setPosition(
                    user.getX() + lookDir.x * 1.5,
                    user.getY() + user.getStandingEyeHeight() + lookDir.y * 0.5,
                    user.getZ() + lookDir.z * 1.5
            );

            // 4. Custom Model Data (ID 1) zuweisen für das lila Modell
            ItemStack fireballStack = new ItemStack(net.minecraft.item.Items.FIRE_CHARGE);
            fireballStack.set(DataComponentTypes.CUSTOM_MODEL_DATA,
                    new net.minecraft.component.type.CustomModelDataComponent(1));

            // Setzt das Item in der Entity (setzt voraus, dass deine Entity FlyingItemEntity implementiert)
            fireball.setItem(fireballStack);

            // 5. In der Welt spawnen
            world.spawnEntity(fireball);

            // 6. Cooldown setzen (5 Sekunden = 100 Ticks)
            user.getItemCooldownManager().set(this, 100);
        }

        user.swingHand(hand);
        return TypedActionResult.success(itemStack, world.isClient());
    }

    // Bonus: Damit das Item im Inventar leuchtet (wie ein verzaubertes Item)
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}