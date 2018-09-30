package xyz.upperlevel.uppercore.config;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConstructor;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.nodes.*;
import xyz.upperlevel.uppercore.config.exceptions.ConfigException;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.config.exceptions.RequiredPropertyNotFoundConfigException;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.upperlevel.uppercore.config.parser.ConfigParser.checkNodeId;
import static xyz.upperlevel.uppercore.config.parser.ConfigParser.checkTag;

public class TrackingConfig extends Config {
    public static BaseConstructor standardConstructor = new YamlConstructor();

    @Getter
    private final MappingNode root;

    @Getter
    @Setter
    private BaseConstructor constructor = standardConstructor;

    public TrackingConfig(@NonNull Node root) {
        checkTag(root, Tag.MAP);
        this.root = (MappingNode) root;
    }

    private Node getRawDirect(MappingNode root, String key) {
        for (NodeTuple node : root.getValue()) {
            if (node.getKeyNode().getNodeId() != NodeId.scalar) continue;
            String nodeKey = ((ScalarNode)node.getKeyNode()).getValue();
            if (key.equals(nodeKey)) return node.getValueNode();
        }
        return null;
    }

    public Node getNode(@NonNull String key) {
        // Map Unfolding check
        MappingNode curr = root;// Current root (may change with map unfolding)
        int off = 0;
        int i;
        while ((i = key.indexOf('.', off)) >= 0) {
            // If we have a map unfolding (ex: "parent.child")
            // Take the various parents and navigate from root down to the real key
            String name = key.substring(off, i);
            Node r = getRawDirect(curr, name);
            if (r == null) return null;
            checkNodeId(r, NodeId.mapping);
            curr = (MappingNode) r;
            off = i + 1;
        }
        return getRawDirect(curr, key.substring(off));
    }

    @Override
    public Object get(String key) {
        Node node = getNode(key);
        if (node == null) return null;
        constructor.setComposer(new FakeComposer(node));
        return constructor.getData();
    }

    @Override
    public Node getYamlNode() {
        return root;
    }

    @Override
    public Stream<String> keys() {
        return root.getValue().stream()
                .filter(t -> t.getKeyNode().getNodeId() == NodeId.scalar)
                .map(t -> ((ScalarNode)t.getKeyNode()).getValue());
    }

    @Override
    public Config getConfig(String key, Config def) {
        Node node = getNode(key);
        if (node == null) return def;
        return new TrackingConfig(node);
    }

    @Override
    public List<Config> getConfigList(String key, List<Config> def) {
        Node node = getNode(key);
        if (node == null) return def;

        checkTag(node, Tag.SEQ);
        SequenceNode seq = (SequenceNode) node;

        return seq.getValue().stream()
                .map(TrackingConfig::new)
                .collect(Collectors.toList());
    }

    // Error tracking

    private Node checkNodeParam(String key) {
        Node node = getNode(key);
        if (node == null) throw new IllegalArgumentException("Cannot find key '" + key + "' in config");
        return node;
    }

    @Override
    protected void checkPropertyNotNull(String key, Object prop) {
        if (prop != null) return;
        throw new RequiredPropertyNotFoundConfigException(getYamlNode(), ImmutableList.of(key));
    }

    @Override
    protected RuntimeException adjustParsingException(String key, InvalidConfigException e) {
        return new ConfigException(
                e.getMessage(),
                checkNodeParam(key).getStartMark()
        );
    }

    @Override
    protected RuntimeException invalidValueTypeException(String key, String expectedType) {
        Node raw = checkNodeParam(key);
        String type;
        if (raw.getNodeId() == NodeId.scalar) {
            type = ((ScalarNode)raw).getValue();
        } else {
            type = raw.getNodeId().name();
        }
        throw new WrongValueConfigException(raw, type, expectedType);
    }

    @Override
    protected RuntimeException invalidConfigException(String key, String cause) {
        throw new ConfigException(
                cause,
                checkNodeParam(key).getStartMark()
        );
    }

    protected static class FakeComposer extends Composer {
        private final Node node;

        public FakeComposer(Node node) {
            super(null, null);
            this.node = node;
        }

        @Override
        public boolean checkNode() {
            return true;
        }

        @Override
        public Node getNode() {
            return node;
        }

        @Override
        public Node getSingleNode() {
            return node;
        }
    }
}
