package net.silvking432.silvkingsmod.world.gen;

public class ModWorldGeneration {
    public static void generateModWorldGen() {
        ModOreGeneration.generateOres();
        ModTreeGeneration.generateTrees();
        ModBushGeneration.generateBushes();
        ModEntitySpawns.addSpawns();
    }
}
