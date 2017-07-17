package xyz.upperlevel.uppercore.gui.config.action.exceptions;

public class RequiredParameterNotFoundException extends IllegalParametersException {

    public RequiredParameterNotFoundException(String parameterName) {
        super(parameterName, "Cannot find parameter " + parameterName);
    }
}
