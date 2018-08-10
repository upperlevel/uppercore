package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.nodes.Node;

public class CannotConvertNumberConfigException extends ConfigException {
    public CannotConvertNumberConfigException(Node node, String rawNum, String format) {
        super(node, "Cannot convert '" + rawNum + "' to " + format);
    }
}
