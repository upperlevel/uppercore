package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.nodes.Node;

public class DuplicatePropertyConfigException extends ConfigException {
    public DuplicatePropertyConfigException(Node node, Node duplicate, String name) {
        super(
                "Property ", duplicate.getStartMark(),
                "has been already declared", node.getStartMark(),
                "Consider removing one", null
        );
    }
}
