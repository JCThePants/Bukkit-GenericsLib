package com.jcwhatever.nucleus.sounds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.file.FileUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Tests {@link SoundManager}.
 */
public class SoundManagerTest {

    private Plugin _plugin = BukkitTest.mockPlugin("dummy");
    private Player _player = BukkitTest.login("dummy");

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();

        Plugin plugin = BukkitTest.mockPlugin("dummy");
        String yml = FileUtils.scanTextFile(SoundManagerTest.class, "/resource-sounds.yml", StandardCharsets.UTF_8);

        YamlDataNode dataNode = new YamlDataNode(plugin, yml);
        dataNode.load();

        SoundManager.load(dataNode);
    }

    /**
     * Make sure {@code #getSounds()} works correctly.
     */
    @Test
    public void testGetSounds() throws Exception {

        List<ResourceSound> sounds = SoundManager.getSounds();

        assertEquals(6, sounds.size());
    }

    /**
     * Make sure {@code #getSound} works correctly.
     */
    @Test
    public void testGetSound() throws Exception {

        ResourceSound sound = SoundManager.getSound("music1");
        assertTrue(sound instanceof MusicSound);
        assertEquals("music1", sound.getName());

        sound = SoundManager.getSound("voice2");
        assertTrue(sound instanceof VoiceSound);
        assertEquals("voice2", sound.getName());

        sound = SoundManager.getSound("effect1");
        assertTrue(sound instanceof EffectSound);
        assertEquals("effect1", sound.getName());
    }

    /**
     * Make sure {@code #getSounds(Player)} works correctly.
     * Also tests {@code #playSound} and {@code #getSounds}.
     */
    @Test
    public void testGetSounds1() throws Exception {

        // establish that there are no sounds playing to the player
        List<ResourceSound> sounds = SoundManager.getSounds(_player);
        assertEquals(0, sounds.size());

        ResourceSound sound = SoundManager.getSound("voice1");

        // play sound and make sure it is returned
        SoundManager.playSound(_plugin, _player, sound, 1.0f);

        sounds = SoundManager.getSounds(_player);
        assertEquals(1, sounds.size());

        // wait for sound to end
        BukkitTest.pause(25);

        // make sure the sound ended
        sounds = SoundManager.getSounds(_player);
        assertEquals(0, sounds.size());
    }

    /**
     * Make sure {@code #getSounds(Class)} works correctly.
     */
    @Test
    public void testGetSounds2() throws Exception {
        List<MusicSound> music = SoundManager.getSounds(MusicSound.class);
        assertEquals(2, music.size());

        List<VoiceSound> voice = SoundManager.getSounds(VoiceSound.class);
        assertEquals(2, voice.size());

        List<EffectSound> effects = SoundManager.getSounds(EffectSound.class);
        assertEquals(2, effects.size());
    }

    /**
     * Make sure {@code #getPlaying} works correctly.
     * Also tests {@code #getSound} and {@code #getPlaying}.
     */
    @Test
    public void testGetPlaying() throws Exception {

        List<Playing> playing = SoundManager.getPlaying(_player);
        assertEquals(0, playing.size());

        ResourceSound sound = SoundManager.getSound("effect1");
        SoundManager.playSound(_plugin, _player, sound, 1.0f);

        // play sound and check if it is returned
        playing = SoundManager.getPlaying(_player);
        assertEquals(1, playing.size());

        BukkitTest.pause(25); // wait for sound to end

        // make sure sound ended
        playing = SoundManager.getPlaying(_player);
        assertEquals(0, playing.size());
    }
}