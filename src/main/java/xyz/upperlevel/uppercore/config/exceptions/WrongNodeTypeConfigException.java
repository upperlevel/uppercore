package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WrongNodeTypeConfigException extends ConfigException {
    public WrongNodeTypeConfigException(Node node, NodeId... expected) {
        super(node, "Wrong type: found " + node.getNodeId() + ", expected: " + Arrays.stream(expected).map(NodeId::name).collect(Collectors.joining(", ")));
    }

    public WrongNodeTypeConfigException(Node node, Tag... expected) {
        super(node, "Wrong type: found " + node.getTag() + ", expected: " + Arrays.stream(expected).map(Tag::getValue).collect(Collectors.joining(", ")));
    }
}
