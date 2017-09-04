package xyz.upperlevel.uppercore.gui.action.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */

/**
 * This is called when a multiple required parameter action is being called using a single parameter
 */
public class BadParameterUseException extends IllegalParametersException {

    public BadParameterUseException() {
        super(null, "Cannot initialize a multiple-parameter action with only one parameter!");
    }
}
