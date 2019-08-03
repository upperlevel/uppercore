package xyz.upperlevel.uppercore.registry;

import com.google.common.collect.ImmutableMap;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegistryTest {

    @Test
    public void basicTest() {
        Registry<?> root = Registry.root();
        Registry<String> registryA = root.registerChild("plugin_a", String.class);
        Registry<Integer> registryB = root.registerChild("plugin_b", Integer.class);

        registryA.register("test", "value");
        assertEquals(registryA.get("test"), "value");
        assertEquals(registryA.find("test"), "value");
        assertEquals(root.find("plugin_a.test"), "value");

        registryB.register("some_int", 3);
        assertSame(registryB.get("some_int"), 3);
        assertSame(registryB.find("some_int"), 3);
        assertSame(root.find("plugin_b.some_int"), 3);

        Registry<String> child = registryA.registerChild("child", String.class);
        assertEquals(registryA.getChild("child"), child);
        child.register("value", "that");
        assertEquals(registryA.find("child.value"), "that");
        assertEquals(root.find("plugin_a.child.value"), "that");

        assertEquals(child.find("@plugin_b.some_int"), 3);
    }

    @Test
    public void visitorTester() {
        RegistryRoot root = new RegistryRoot();
        Registry<String> registryA = root.registerChild("plugin", String.class);

        registryA.register("a1", "A");
        Registry<Integer> childA = registryA.registerChild("achild", Integer.class);
        childA.register("test", 4);

        AtomicInteger i = new AtomicInteger(0);

        // Normal test
        root.visit(new RegistryVisitor() {
            @Override
            public VisitResult preVisitRegistry(Registry<?> registry) {
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult visitEntry(String name, Object value) {
                i.incrementAndGet();
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult postVisitRegistry(Registry<?> registry) {
                return VisitResult.CONTINUE;
            }
        });
        assertEquals(2, i.get());


        // Test skip
        i.set(0);
        root.visit(new RegistryVisitor() {
            @Override
            public VisitResult preVisitRegistry(Registry<?> registry) {
                return registry == childA ? VisitResult.SKIP : VisitResult.CONTINUE;
            }

            @Override
            public VisitResult visitEntry(String name, Object value) {
                i.incrementAndGet();
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult postVisitRegistry(Registry<?> registry) {
                return VisitResult.CONTINUE;
            }
        });
        assertEquals(1, i.get());

        // Test terminate
        i.set(0);
        root.visit(new RegistryVisitor() {
            @Override
            public VisitResult preVisitRegistry(Registry<?> registry) {
                return registry == childA ? VisitResult.TERMINATE : VisitResult.CONTINUE;
            }

            @Override
            public VisitResult visitEntry(String name, Object value) {
                i.incrementAndGet();
                return VisitResult.CONTINUE;
            }

            @Override
            public VisitResult postVisitRegistry(Registry<?> registry) {
                return VisitResult.CONTINUE;
            }
        });
        assertEquals(0, i.get());
    }

    @Test
    public void pathTester() {
        String plugin = "pLuGin";
        RegistryRoot root = new RegistryRoot();
        Registry<?> pluginRoot = root.registerChild(plugin);
        assertEquals("plugin", pluginRoot.getPath());
        Registry<?> folder = pluginRoot.registerChild("folder");
        assertEquals("plugin.folder", folder.getPath());
        Registry<String> strings = folder.registerChild("strings", String.class);
        assertEquals("plugin.folder.strings", strings.getPath());
        // Just to test a bit more, there's no deep meaning in adding a integer child to a string registry
        Registry<Integer> strints = strings.registerChild("integers", Integer.class);
        assertEquals("plugin.folder.strings.integers", strints.getPath());
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void folderChildTest() {
        String plugin = "plUGiN";
        RegistryRoot root = new RegistryRoot();
        Registry<?> pluginRoot = root.registerChild(plugin);
        ((Registry<String>)pluginRoot).register("Child", "Test");
    }
}
