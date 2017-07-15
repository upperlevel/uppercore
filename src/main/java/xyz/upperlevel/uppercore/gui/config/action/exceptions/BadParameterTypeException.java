package xyz.upperlevel.uppercore.gui.config.action.exceptions;

import lombok.Getter;

public class BadParameterTypeException extends IllegalParametersException {
    @Getter
    private final Class<?> expected, found;
    public BadParameterTypeException(String parameterName, Class<?> expected, Class<?> found) {
        super(parameterName, "Parameter " + parameterName + " has bad type: expected: " + expected.getSimpleName() + ", found: " + found.getSimpleName());
        this.expected = expected;
        this.found = found;
    }
}
