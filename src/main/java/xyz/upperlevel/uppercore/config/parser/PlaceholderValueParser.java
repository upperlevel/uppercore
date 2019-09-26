package xyz.upperlevel.uppercore.config.parser;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.util.TextUtil;

public class PlaceholderValueParser extends ConfigParser {
    private final ConfigParser valueParser;
    private final boolean translateColors;

    public PlaceholderValueParser(ConfigParser valueParser) {
        this(valueParser, valueParser.getHandleClass().equals(String.class));
    }

    public PlaceholderValueParser(ConfigParser valueParser, boolean translateColors) {
        super(PlaceholderValue.class);
        this.valueParser = valueParser;
        this.translateColors = translateColors;
    }

    @Override
    public PlaceholderValue parse(Node root) {
        checkNodeId(root, NodeId.scalar);
        ScalarNode node = (ScalarNode) root;// TODO: support more complex types like PlaceholderValue<List<String>> or similar
        if (translateColors) {
            node = replaceValue(node, TextUtil.translatePlain(node.getValue()));
        }
        if (!PlaceholderUtil.hasPlaceholders(node.getValue())) {
            return PlaceholderValue.fake(valueParser.parse(node));
        } else {
            // Replace the node's value with the resolved placeholders value
            ScalarNode n = node;
            return (player, local) ->
                    valueParser.parse(replaceValue(n, PlaceholderUtil.resolve(player, n.getValue(), local)));
        }
    }
}
