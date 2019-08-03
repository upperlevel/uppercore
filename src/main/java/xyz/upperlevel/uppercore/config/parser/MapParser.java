package xyz.upperlevel.uppercore.config.parser;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;

import java.util.Map;
import java.util.function.Supplier;

public class MapParser<T extends Map<Object, Object>> extends ConfigParser {
    private final Supplier<? extends T> mapSupplier;
    private final ConfigParser keyParser, valueParser;

    public MapParser(Class<T> handleClass, Supplier<? extends T> mapSupplier, ConfigParser keyParser, ConfigParser valueParser) {
        super(handleClass);
        this.mapSupplier = mapSupplier;
        this.valueParser = valueParser;
        this.keyParser = keyParser;
    }

    @Override
    public T parse(Node rawNode) {
        checkNodeId(rawNode, NodeId.mapping);
        MappingNode node = (MappingNode) rawNode;
        T map = mapSupplier.get();
        for (NodeTuple entry : node.getValue()) {
            map.put(keyParser.parse(entry.getKeyNode()), valueParser.parse(entry.getValueNode()));
        }

        return map;
    }
}
