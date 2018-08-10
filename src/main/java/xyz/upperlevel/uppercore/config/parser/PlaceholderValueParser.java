package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

public class PlaceholderValueParser extends ConfigParser<PlaceholderValue> {
    private final ConfigParser valueParser;

    public PlaceholderValueParser(ConfigParser valueParser) {
        super(PlaceholderValue.class);
        this.valueParser = valueParser;
    }

    @Override
    public PlaceholderValue parse(Plugin plugin, Node root) {
        checkNodeId(root, NodeId.scalar);
        ScalarNode node = (ScalarNode) root;// TODO: support more complex types like PlaceholderValue<List<String>> or similar
        //TODO Test Translate colors and such
        if (!PlaceholderUtil.hasPlaceholders(node.getValue())) {
            return PlaceholderValue.fake(valueParser.parse(plugin, node));
        } else {
            // Replace the node's value with the resolved placeholders value
            return (player, local) ->
                    valueParser.parse(plugin, replaceValue(node, PlaceholderUtil.resolve(player, node.getValue(), local)));
        }
    }
}
