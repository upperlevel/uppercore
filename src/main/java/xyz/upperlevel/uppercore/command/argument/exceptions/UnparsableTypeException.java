package xyz.upperlevel.uppercore.command.argument.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
public class UnparsableTypeException extends RuntimeException {

    public UnparsableTypeException(Class<?> type) {
        this(type.getName());
    }

    public UnparsableTypeException(String message) {
        super(message);
    }
}
