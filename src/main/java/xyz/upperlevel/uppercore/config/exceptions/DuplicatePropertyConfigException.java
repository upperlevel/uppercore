package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.nodes.Node;

public class DuplicatePropertyConfigException extends ConfigException {
    public DuplicatePropertyConfigException(Node node, Node duplicate, String name) {
        super(node, "Duplicate property '" + name + "', (" + duplicate.getStartMark() + " -> " + duplicate.getEndMark() + ")");
    }
}
