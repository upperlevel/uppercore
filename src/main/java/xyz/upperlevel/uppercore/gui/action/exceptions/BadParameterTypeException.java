package xyz.upperlevel.uppercore.gui.action.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
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
