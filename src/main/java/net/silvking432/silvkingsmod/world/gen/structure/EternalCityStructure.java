package net.silvking432.silvkingsmod.world.gen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import net.silvking432.silvkingsmod.world.gen.ModStructures;
import net.silvking432.silvkingsmod.world.gen.generators.EternalCityGenerator;

public class EternalCityStructure extends Structure {
    public static final MapCodec<EternalCityStructure> CODEC = createCodec(EternalCityStructure::new);

    public EternalCityStructure(Structure.Config config) {
        super(config);
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        return blockPos.getY() < 60
                ? Optional.empty()
                : Optional.of(new Structure.StructurePosition(blockPos, collector -> this.addPieces(collector, blockPos, blockRotation, context)));
    }

    private void addPieces(StructurePiecesCollector collector, BlockPos pos, BlockRotation rotation, Structure.Context context) {
        List<StructurePiece> list = Lists.newArrayList();
        EternalCityGenerator.addPieces(context.structureTemplateManager(), pos, rotation, list, context.random());
        list.forEach(collector::addPiece);
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.ETERNAL_CITY;
    }
}
