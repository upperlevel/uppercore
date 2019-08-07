package xyz.upperlevel.uppercore.config.parser;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import xyz.upperlevel.uppercore.config.exceptions.WrongNodeTypeConfigException;

import java.io.Reader;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

public abstract class ConfigParser {
    public static Yaml defaultYaml = new Yaml();
    private static final MethodHandle styleGetter;

    private final Type handleType;

    static {
        try {
            Field reflectedField = ScalarNode.class.getDeclaredField("style");
            reflectedField.setAccessible(true);
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            styleGetter = lookup.unreflectGetter(reflectedField);
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize style getter for snakeyaml ScalarNode", e);
        }
    }

    public ConfigParser(Type handleType) {
        this.handleType = handleType;
    }

    public abstract Object parse(Node root);

    public Object parse(Reader reader) {
        return parse(defaultYaml.compose(reader));
    }

    public Type getHandleClass() {
        return handleType;
    }

    // ---------------- Helper methods ----------------

    public static void checkTag(Node node, Tag expected) {
        if (node.getTag() != expected) {
            throw new WrongNodeTypeConfigException(node, expected);
        }
    }

    public static void checkTag(Node node, Collection<Tag> expected) {
        if (!expected.contains(node.getTag())) {
            throw new WrongNodeTypeConfigException(node, expected.toArray(new Tag[0]));
        }
    }

    public static void checkNodeId(Node node, NodeId expectedId) {
        if (expectedId != node.getNodeId()) {
            throw new WrongNodeTypeConfigException(node, expectedId);
        }
    }

    public static void checkNodeId(Node node, Collection<NodeId> expectedIds) {
        if (!expectedIds.contains(node.getNodeId())) {
            throw new WrongNodeTypeConfigException(node, expectedIds.toArray(new NodeId[0]));
        }
    }

    public static ScalarNode replaceValue(ScalarNode original, String newValue) {
        DumperOptions.ScalarStyle style;
        try {
            style = (DumperOptions.ScalarStyle) styleGetter.invokeExact(original);
        } catch (Throwable t) {
            throw new RuntimeException("Cannot invoke style getter", t);
        }
        return new ScalarNode(original.getTag(), true, newValue, original.getStartMark(), original.getEndMark(), style);
    }
}
