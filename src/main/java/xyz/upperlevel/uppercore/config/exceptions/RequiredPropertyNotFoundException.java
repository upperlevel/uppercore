package xyz.upperlevel.uppercore.config.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;

@Getter
public class RequiredPropertyNotFoundException extends InvalidConfigurationException {
    private final String property;

    public RequiredPropertyNotFoundException(String key, String... localizers) {
        super("Cannot find property \"" + key + "\"", localizers);
        this.property = key;
    }
}
