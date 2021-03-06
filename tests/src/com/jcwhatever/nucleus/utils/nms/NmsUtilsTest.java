package com.jcwhatever.nucleus.utils.nms;

import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.v1_8_R3.MockPlugin;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;

public class NmsUtilsTest {

    @Test
    public void testPatternVersion() {

        String testVersion = "net.minecraft.server.v1_8_R1.";


        Matcher matcher = NmsUtils.PATTERN_VERSION.matcher(testVersion);
        assertEquals(true, matcher.matches());
        assertEquals("v1_8_R1", matcher.group(1));
    }

    @Test
    public void testGetNmsVersion() throws Exception {

        assertEquals(BukkitTester.NMS_TEST_VERSION, NmsUtils.getNmsVersion());
    }

    @Test
    public void testIsVersionCompatible() throws Exception {

        assertEquals(true, NmsUtils.isVersionCompatible("v1_8_R3"));
        assertEquals(false, NmsUtils.isVersionCompatible("v1_8_R4"));
        assertEquals(false, NmsUtils.isVersionCompatible("v1_9_R1"));
        assertEquals(false, NmsUtils.isVersionCompatible("v2_8_R1"));
        assertEquals(false, NmsUtils.isVersionCompatible("1_8_R1"));

    }

    @Test
    public void testEnforceNmsVersion() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy").enable();

        NmsUtils.enforceNmsVersion(plugin, "a");

        assertEquals(false, plugin.isEnabled());

    }
}