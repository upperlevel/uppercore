package xyz.upperlevel.uppercore.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConstructor;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.nodes.*;

import java.util.List;
import java.util.stream.Collectors;

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

    private Node getRaw(String key) {
        for (NodeTuple node : root.getValue()) {
            if (node.getKeyNode().getTag() != Tag.STR) continue;
            String nodeKey = ((ScalarNode)node.getKeyNode()).getValue();
            if (key.equals(nodeKey)) return node.getValueNode();
        }
        return null;
    }

    @Override
    public Object get(String key) {
        Node node = getRaw(key);
        constructor.setComposer(new FakeComposer(node));
        return constructor.getData();
    }

    @Override
    public Node getYamlNode() {
        return root;
    }

    @Override
    public Config getConfig(String key, Config def) {
        Node node = getRaw(key);
        if (node == null) return def;
        return new TrackingConfig(node);
    }

    @Override
    public List<Config> getConfigList(String key, List<Config> def) {
        Node node = getRaw(key);
        if (node == null) return def;

        checkTag(node, Tag.SEQ);
        SequenceNode seq = (SequenceNode) node;

        return seq.getValue().stream()
                .map(TrackingConfig::new)
                .collect(Collectors.toList());
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
