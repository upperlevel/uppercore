package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public class ConfigException extends RuntimeException {
    private final Mark start, end;

    public ConfigException(Mark start, Mark end, String message) {
        super(message + ";" + start);
        this.start = start;
        this.end = end;
    }

    public ConfigException(Node node, String message) {
        this(node.getStartMark(), node.getEndMark(), message);
    }

    @Override
    public String toString() {
        StringBuilder lines = new StringBuilder();
        lines.append(start);
        lines.append('\n');
        lines.append(getMessage());
        return lines.toString();
    }
}
