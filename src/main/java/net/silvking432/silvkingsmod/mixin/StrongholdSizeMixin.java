package net.silvking432.silvkingsmod.mixin;

import net.minecraft.structure.StrongholdGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(StrongholdGenerator.class)
public class StrongholdSizeMixin {

    @ModifyConstant(method = "pieceGenerator", constant = @Constant(intValue = 50))
    private static int silvkingsmod$increaseMaxChainLength(int original) {
        return 500;
    }

    @ModifyConstant(method = "pieceGenerator", constant = @Constant(intValue = 112))
    private static int silvkingsmod$increaseMaxDistance(int original) {
        return 1024;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private static void silvkingsmod$boostRoomLimits(CallbackInfo ci) {
        try {
            Field allPiecesField;
            try {
                allPiecesField = StrongholdGenerator.class.getDeclaredField("ALL_PIECES");
            } catch (NoSuchFieldException e) {
                allPiecesField = StrongholdGenerator.class.getDeclaredField("field_31628");
            }

            allPiecesField.setAccessible(true);
            Object[] pieces = (Object[]) allPiecesField.get(null);

            for (Object piece : pieces) {
                Class<?> pieceClass = piece.getClass();

                modifyPrivateInt(piece, pieceClass, "limit", " ", 100);
                modifyPrivateInt(piece, pieceClass, "weight", "field_15278", 200);
            }
        } catch (Exception e) {
            System.err.println("[SilvKingsTitanMod] An Error Occured: " + e.getMessage());
        }
    }

    @Unique
    private static void modifyPrivateInt(Object instance, Class<?> clazz, String name, String intermediary, int newValue) {
        try {
            Field field;
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = clazz.getDeclaredField(intermediary);
            }
            field.setAccessible(true);

            if (name.equals("limit") || intermediary.equals("field_15277")) {
                int old = field.getInt(instance);
                if (old > 0) field.setInt(instance, newValue);
            } else {
                field.setInt(instance, newValue);
            }
        } catch (Exception ignored) {}
    }
}