package xyz.upperlevel.uppercore.config.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;

@Getter
public class InvalidVauleConfigException extends InvalidConfigurationException {
    private final String key;
    private final Object value;
    private final String expected;


    public InvalidVauleConfigException(String key, Object value, String expected, String... localizers) {
        super("Invalid value in '" + key + "' (found '" + value.getClass().getSimpleName() + "', expected '" + expected + "')", localizers);
        this.key = key;
        this.value = value;
        this.expected = expected;
    }
}
