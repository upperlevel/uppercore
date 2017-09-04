package xyz.upperlevel.uppercore.gui.action.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
public class RequiredParameterNotFoundException extends IllegalParametersException {

    public RequiredParameterNotFoundException(String parameterName) {
        super(parameterName, "Cannot find parameter " + parameterName);
    }
}
