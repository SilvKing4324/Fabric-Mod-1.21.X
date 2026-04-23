package net.silvking432.silvkingsmod.world.gen;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.world.gen.generators.EternalCityGenerator;
import net.silvking432.silvkingsmod.world.gen.structure.EternalCityStructure;

public class ModStructures {

    public static final StructureType<EternalCityStructure> ETERNAL_CITY =
            registerStructure("eternal_city", EternalCityStructure.CODEC);

    public static final StructurePieceType ETERNAL_CITY_PIECE = registerPiece("eternal_city_piece",
            EternalCityGenerator.Piece::new);


    private static <S extends Structure> StructureType<S> registerStructure(String name, MapCodec<S> codec) {
        Identifier id = Identifier.of(SilvKingsMod.MOD_ID, name);
        return Registry.register(Registries.STRUCTURE_TYPE, id, () -> codec);
    }


    private static StructurePieceType registerPiece(String name, StructurePieceType type) {
        return Registry.register(Registries.STRUCTURE_PIECE, Identifier.of(SilvKingsMod.MOD_ID, name), type);
    }

    public static void registerModStructures() {
        SilvKingsMod.LOGGER.info("Registering Structures for " + SilvKingsMod.MOD_ID);
    }
}