package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.nodes.Node;

public class PropertyNotFoundParsingException extends ConfigException {

    public PropertyNotFoundParsingException(Node node, String property, Class<?> clazz) {
        super("Cannot find property '" + property + "' in class " + clazz.getSimpleName(), node);
    }
}
