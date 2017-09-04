package xyz.upperlevel.uppercore.gui.action.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;

public class IllegalParametersException extends RuntimeException {

    @Getter
    private final String parameterName;

    public IllegalParametersException(String parameterName, String message) {
        super(message);
        this.parameterName = parameterName;
    }
}
