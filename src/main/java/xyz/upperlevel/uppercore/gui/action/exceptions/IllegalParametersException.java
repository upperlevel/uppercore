package xyz.upperlevel.uppercore.gui.action.exceptions;

import lombok.Getter;

public class IllegalParametersException extends RuntimeException {

    @Getter
    private final String parameterName;

    public IllegalParametersException(String parameterName, String message) {
        super(message);
        this.parameterName = parameterName;
    }
}
