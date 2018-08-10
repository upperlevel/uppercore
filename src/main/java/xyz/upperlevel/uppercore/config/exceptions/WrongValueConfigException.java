package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.nodes.Node;

public class WrongValueConfigException extends ConfigException {
    public WrongValueConfigException(Node node, String found, String expectedType) {
        super(node, "Expected " + expectedType + " but found '" + found + "'");
    }
}
