package net.silvking432.silvkingsmod.sound;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class ModSounds {

    public static final SoundEvent CHISEL_USE = registerSoundEvent("chisel_use");

    public static final SoundEvent MAGIC_BLOCK_BREAK = registerSoundEvent("magic_block_break");
    public static final SoundEvent MAGIC_BLOCK_STEP = registerSoundEvent("magic_block_step");
    public static final SoundEvent MAGIC_BLOCK_PLACE = registerSoundEvent("magic_block_place");
    public static final SoundEvent MAGIC_BLOCK_HIT = registerSoundEvent("magic_block_hit");
    public static final SoundEvent MAGIC_BLOCK_FALL = registerSoundEvent("magic_block_fall");

    public static final BlockSoundGroup MAGIC_BLOCK_SOUNDS = new BlockSoundGroup(1f,1f,
            MAGIC_BLOCK_BREAK, MAGIC_BLOCK_FALL, MAGIC_BLOCK_HIT, MAGIC_BLOCK_STEP, MAGIC_BLOCK_PLACE);

    public static final SoundEvent NECRON_DOOM = registerSoundEvent("necron_doom");
    public static final RegistryKey<JukeboxSong> NECRON_DOOM_KEY = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(SilvKingsMod.MOD_ID,"necron_doom"));

    public static final SoundEvent NECRON_INTRO1 = registerSoundEvent("music_necron_doom_intro1");
    public static final SoundEvent NECRON_LOOP1 = registerSoundEvent("music_necron_doom_loop1");

    public static final SoundEvent NECRON_INTRO2 = registerSoundEvent("music_necron_doom_intro2");
    public static final SoundEvent NECRON_LOOP2 = registerSoundEvent("music_necron_doom_loop2");

    public static final SoundEvent NECRON_INTRO3 = registerSoundEvent("music_necron_doom_intro3");
    public static final SoundEvent NECRON_LOOP3 = registerSoundEvent("music_necron_doom_loop3");

    public static final SoundEvent NECRON_INTRO4 = registerSoundEvent("music_necron_doom_intro4");
    public static final SoundEvent NECRON_LOOP4 = registerSoundEvent("music_necron_doom_loop4");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(SilvKingsMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT,id,SoundEvent.of(id));

    }

    public static void registerSounds() {
        SilvKingsMod.LOGGER.info("Registering Sounds for " + SilvKingsMod.MOD_ID);
    }
}
