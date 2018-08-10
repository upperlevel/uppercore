package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.nodes.Node;

import java.util.List;

public class RequiredPropertyNotFoundConfigException extends ConfigException {
    public RequiredPropertyNotFoundConfigException(Node node, List<String> propertyNames) {
        super(node, "Cannot find required properties: '" + String.join("', '", propertyNames) + "'");
    }
}
