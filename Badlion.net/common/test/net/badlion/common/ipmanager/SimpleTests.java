package net.badlion.common.ipmanager;

import net.badlion.common.IPManager;
import org.junit.Assert;
import org.junit.Test;

public class SimpleTests {

    public static class TestIPManager {

        @Test
        public void testBasicLookups() {
            IPManager manager = new IPManager();
            manager.addIP("127.0.0.1", IPManager.Access.DENIED);
            Assert.assertEquals(manager.getAccess("127.0.0.1").ordinal(), IPManager.Access.DENIED.ordinal());
            Assert.assertEquals(manager.getAccess("128.0.0.1").ordinal(), IPManager.Access.ALLOWED.ordinal());
            Assert.assertEquals(manager.getAccess("127.0.0.10").ordinal(), IPManager.Access.ALLOWED.ordinal());
            Assert.assertEquals(manager.getAccess("127.0.1.0").ordinal(), IPManager.Access.ALLOWED.ordinal());

            manager.addIP("128.0.0.0/8", IPManager.Access.DENIED);
            Assert.assertEquals(IPManager.Access.DENIED.ordinal(), manager.getAccess("128.0.0.1").ordinal());
            manager.addIP("128.0.0.1", IPManager.Access.ALLOWED);
            Assert.assertEquals(IPManager.Access.ALLOWED.ordinal(), manager.getAccess("128.0.0.1").ordinal());

            long b4 = System.nanoTime();
            manager.getAccess("128.0.0.1").ordinal();
            long after = System.nanoTime();
            System.out.println("after " + (after - b4));
        }
    }
}
