package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.dummy.DummyPlugin;
import com.jcwhatever.nucleus.NucleusInit;
import com.jcwhatever.nucleus.collections.java.MultimapTest;

import org.junit.Test;

public class TimedSetMultimapTest {


    @Test
    public void basicTest() {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedSetMultimap<String, String> map = new TimedSetMultimap<String, String>(plugin);

        MultimapTest<String> test = new MultimapTest<>(map, "va", "vb", "vc");
        test.run();
    }

    @Test
    public void testEntryLifespan() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedSetMultimap<String, String> map = new TimedSetMultimap<String, String>(plugin);

        map.put("a", "b", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 100) {

            assertEquals(map.containsKey("a"), System.currentTimeMillis() < expires);

            Thread.sleep(5);
        }
    }

}