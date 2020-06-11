package xyz.upperlevel.uppercore.command.functional.parameter;

import lombok.Getter;

import java.util.List;

public class ParameterParseException extends RuntimeException {
    @Getter
    private final Class<?> type;

    @Getter
    private final List<String> args;

    public ParameterParseException(Class<?> type, List<String> args) {
        this.type = type;
        this.args = args;
    }
}
