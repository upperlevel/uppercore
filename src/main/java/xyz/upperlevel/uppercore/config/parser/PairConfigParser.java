package xyz.upperlevel.uppercore.config.parser;

import com.google.common.collect.ImmutableList;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.*;
import xyz.upperlevel.uppercore.config.exceptions.ConfigException;
import xyz.upperlevel.uppercore.util.Pair;

import java.lang.reflect.Type;
import java.util.List;

public class PairConfigParser extends ConfigParser {
    private final ConfigParser firstParser, secondParser;

    public PairConfigParser(Type handleType, ConfigParser firstParser, ConfigParser secondParser) {
        super(handleType);
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public Pair<?, ?> parse(Plugin plugin, Node root) {
        checkNodeId(root, ImmutableList.of(NodeId.mapping, NodeId.sequence));
        if (root.getNodeId() == NodeId.mapping) {
            List<NodeTuple> val = ((MappingNode)root).getValue();
            if (val.isEmpty()) {
                throw new ConfigException("No elements in pair!", root);
            }
            if (val.size() > 1) {
                throw new ConfigException("Too many values for a Pair!", val.get(1).getKeyNode());
            }
            NodeTuple t = val.get(0);
            Object first = firstParser.parse(plugin, t.getKeyNode());
            Object second = secondParser.parse(plugin, t.getValueNode());
            return new Pair<>(first, second);
        } else if (root.getNodeId() == NodeId.sequence) {
            List<Node> val = ((SequenceNode)root).getValue();
            if (val.size() < 2) {
                throw new ConfigException("A Pair should have at least 2 elements!", root);
            }
            if (val.size() > 2) {
                throw new ConfigException("Too many elements for a pair!", root);
            }
            Object first = firstParser.parse(plugin, val.get(0));
            Object second = secondParser.parse(plugin, val.get(1));
            return new Pair<>(first, second);
        } else {
            throw new IllegalStateException("Unreachable");
        }
    }
}
