package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import xyz.upperlevel.uppercore.config.exceptions.WrongNodeTypeConfigException;

import java.io.Reader;
import java.util.Collection;

public abstract class ConfigParser<T> {
    public static Yaml defaultYaml = new Yaml();

    private final Class<T> handleClass;

    public ConfigParser(Class<T> handleClass) {
        this.handleClass = handleClass;
    }

    public abstract T parse(Plugin plugin, Node root);

    public T parse(Plugin plugin, Reader reader) {
        return parse(plugin, defaultYaml.compose(reader));
    }

    public Class<T> getHandleClass() {
        return handleClass;
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
        if (expectedIds.contains(node.getNodeId())) {
            throw new WrongNodeTypeConfigException(node, expectedIds.toArray(new NodeId[0]));
        }
    }

    public static ScalarNode replaceValue(ScalarNode original, String newValue) {
        return new ScalarNode(original.getTag(), original.isResolved(), newValue, original.getStartMark(),
                original.getEndMark(), original.getStyle());
    }
}
