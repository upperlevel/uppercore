package xyz.upperlevel.uppercore.config.exceptions;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public class ConfigException extends RuntimeException {
    private final String context;
    private final Mark contextMark;
    private final String problem;
    private final Mark problemMark;
    private final String note;

    public ConfigException(String context, Mark contextMark, String problem, Mark problemMark, String note, Throwable cause) {
        super(context + "; " + problem + problemMark, cause);
        this.context = context;
        this.contextMark = contextMark;
        this.problem = problem;
        this.problemMark = problemMark;
        this.note = note;
    }

    public ConfigException(String context, Mark contextMark, String problem, Mark problemMark) {
        this(context, contextMark, problem, problemMark, null, null);
    }

    public ConfigException(String problem, Mark problemMark) {
        this(null, null, problem, problemMark, null, null);
    }

    public ConfigException(String problem, Node problemNode) {
        this(problem, problemNode.getStartMark());
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder lines = new StringBuilder();
        if (context != null) {
            lines.append(context);
            lines.append("\n");
        }
        if (contextMark != null
                && (problem == null || problemMark == null
                || contextMark.getName().equals(problemMark.getName())
                || (contextMark.getLine() != problemMark.getLine()) || (contextMark
                .getColumn() != problemMark.getColumn()))) {
            lines.append(contextMark.toString());
            lines.append("\n");
        }
        if (problem != null) {
            lines.append(problem);
            lines.append("\n");
        }
        if (problemMark != null) {
            lines.append(problemMark.toString());
            lines.append("\n");
        }
        if (note != null) {
            lines.append(note);
            lines.append("\n");
        }
        return lines.toString();
    }
}
