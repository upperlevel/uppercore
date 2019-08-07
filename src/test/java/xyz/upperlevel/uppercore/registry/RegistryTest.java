package xyz.upperlevel.uppercore.registry;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class RegistryTest {

    @Test
    public void basicTest() {
        Registry root = Registry.root();
        Registry registryA = root.registerFolder("plugin_a");
        Registry registryB = root.registerFolder("plugin_b");

        registryA.register("test", "value");
        assertEquals("value", registryA.get("test"));
        assertEquals("value", root.get("plugin_a.test"));
        assertEquals("value", registryA.get("@plugin_a.test"));

        registryB.register("some_int", 3);
        assertSame(registryB.get("some_int"), 3);
        assertSame(root.get("plugin_b.some_int"), 3);

        Registry child = registryA.registerFolder("child");
        assertEquals(child, registryA.get("child"));
        child.register("value", "that");
        assertEquals("that", registryA.get("child.value"));
        assertEquals("that", root.get("plugin_a.child.value"));

        assertEquals(3, (int) child.get("@plugin_b.some_int"));
    }

    @Test
    public void visitorTester() {
        Registry root = Registry.root();
        Registry registryA = root.registerFolder("plugin");

        registryA.register("a1", "A");
        Registry childA = registryA.registerFolder("achild");
        childA.register("test", 4);

        AtomicInteger i = new AtomicInteger(0);

        // Normal test
        root.visit(new RegistryVisitor() {
            @Override
            public VisitResult preVisitRegistry(Registry registry) {
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult visitEntry(String name, Object value) {
                i.incrementAndGet();
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult postVisitRegistry(Registry registry) {
                return VisitResult.CONTINUE;
            }
        });
        assertEquals(2, i.get());


        // Test skip
        i.set(0);
        root.visit(new RegistryVisitor() {
            @Override
            public VisitResult preVisitRegistry(Registry registry) {
                return registry == childA ? VisitResult.SKIP : VisitResult.CONTINUE;
            }

            @Override
            public VisitResult visitEntry(String name, Object value) {
                i.incrementAndGet();
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult postVisitRegistry(Registry registry) {
                return VisitResult.CONTINUE;
            }
        });
        assertEquals(1, i.get());

        // Test terminate
        i.set(0);
        root.visit(new RegistryVisitor() {
            @Override
            public VisitResult preVisitRegistry(Registry registry) {
                return registry == childA ? VisitResult.SKIP : VisitResult.CONTINUE;
            }

            @Override
            public VisitResult visitEntry(String name, Object value) {
                if (name.equals("test")) i.incrementAndGet();
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult postVisitRegistry(Registry registry) {
                return VisitResult.CONTINUE;
            }
        });
        assertEquals(0, i.get());
    }

    @Test
    public void pathTester() {
        String plugin = "pLuGin";
        Registry root = Registry.root();
        Registry pluginRoot = root.registerFolder(plugin);
        assertEquals("plugin", pluginRoot.getPath());
        Registry folder = pluginRoot.registerFolder("folder");
        assertEquals("plugin.folder", folder.getPath());
        Registry strings = folder.registerFolder("strings");
        assertEquals("plugin.folder.strings", strings.getPath());
        // Just to test a bit more, there's no deep meaning in adding a integer child to a string registry
        Registry strints = strings.registerFolder("integers");
        assertEquals("plugin.folder.strings.integers", strints.getPath());
    }

    @Test
    public void folderCreationTest() {
        Registry root = Registry.root();
        root.register("a.b.c.d.e", "f");
        assertEquals("a", root.getFolders().get(0).getName());
        Registry a = root.get("a");
        assertEquals("b", a.getFolders().get(0).getName());
        assertEquals("f", a.get("b.c.d.e"));
    }
}
