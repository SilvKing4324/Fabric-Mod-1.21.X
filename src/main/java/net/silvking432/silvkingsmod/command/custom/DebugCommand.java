package net.silvking432.silvkingsmod.command.custom;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.item.ModItems;

import java.lang.reflect.Field;

import static net.minecraft.server.command.CommandManager.literal;

public class DebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("silvkingsTitanModDebug")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("summonAllMobs")
                        .executes(context -> spawnAllInternal(context.getSource(), false)))
                .then(literal("summonAndFreezeAllMobs")
                        .executes(context -> spawnAllInternal(context.getSource(), true)))
                .then(literal("dropAllItems")
                        .executes(context -> giveAllItems(context.getSource())))
                .then(literal("dropAllBlocks")
                        .executes(context -> giveAllBlocks(context.getSource())))
        );
    }

    private static int spawnAllInternal(ServerCommandSource source, boolean freeze) {
        BlockPos origin = BlockPos.ofFloored(source.getPosition());
        ServerWorld world = source.getWorld();
        Random random = world.getRandom();
        int spread = 30;

        String message = freeze ? "Summoning and freezing all custom mobs..." : "Summoning all custom mobs...";
        source.sendFeedback(() -> Text.literal(message), true);

        for (Field field : ModEntities.class.getDeclaredFields()) {
            try {
                if (field.getType().equals(EntityType.class)) {
                    EntityType<?> entityType = (EntityType<?>) field.get(null);

                    if (entityType != null) {

                        if (entityType == ModEntities.TITANIUM_TNT_ENTITY) {
                            continue;
                        }
                        int offsetX = random.nextBetween(-spread, spread);
                        int offsetZ = random.nextBetween(-spread, spread);
                        BlockPos spawnPos = origin.add(offsetX, 0, offsetZ);

                        Entity entity = entityType.spawn(world, spawnPos, SpawnReason.COMMAND);

                        if (freeze && entity instanceof MobEntity mob) {
                            mob.setAiDisabled(true);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                source.sendError(Text.literal("Failed to access field: " + field.getName()));
            }
        }
        return 1;
    }

    private static int giveAllItems(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        source.sendFeedback(() -> Text.literal("Giving all custom items..."), true);

        for (Field field : ModItems.class.getDeclaredFields()) {
            try {
                if (Item.class.isAssignableFrom(field.getType())) {
                    Item item = (Item) field.get(null);

                    if (item != null) {
                        ItemStack stack = new ItemStack(item);
                        player.getInventory().offerOrDrop(stack);
                    }
                }
            } catch (IllegalAccessException e) {
                source.sendError(Text.literal("Failed to access item field: " + field.getName()));
            }
        }
        return 1;
    }

    private static int giveAllBlocks(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        source.sendFeedback(() -> Text.literal("Giving all custom blocks..."), true);

        for (Field field : ModBlocks.class.getDeclaredFields()) {
            try {
                if (Block.class.isAssignableFrom(field.getType())) {
                    Block block = (Block) field.get(null);

                    if (block != null) {
                        Item item = block.asItem();

                        if (item != Items.AIR) {
                            player.getInventory().offerOrDrop(new ItemStack(item));
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                source.sendError(Text.literal("Failed to access block field: " + field.getName()));
            }
        }
        return 1;
    }
}