package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;

import java.util.Map;
import java.util.function.Supplier;

public class MapParser<T extends Map> extends ConfigParser<T> {
    private final Supplier<Map> mapSupplier;
    private final ConfigParser keyParser, valueParser;

    public MapParser(Class<T> handleClass, Supplier<Map> mapSupplier, ConfigParser keyParser, ConfigParser valueParser) {
        super(handleClass);
        this.mapSupplier = mapSupplier;
        this.valueParser = valueParser;
        this.keyParser = keyParser;
    }

    @Override
    public T parse(Plugin plugin, Node rawNode) {
        checkNodeId(rawNode, NodeId.mapping);
        MappingNode node = (MappingNode) rawNode;
        Map map = mapSupplier.get();
        for (NodeTuple entry : node.getValue()) {
            map.put(keyParser.parse(plugin, entry.getKeyNode()), valueParser.parse(plugin, entry.getValueNode()));
        }

        return (T) map;
    }
}