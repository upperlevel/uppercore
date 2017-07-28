package xyz.upperlevel.uppercore.config.exceptions;

import lombok.Getter;

@Getter
public class RequiredPropertyNotFoundException extends InvalidConfigurationException {
    private final String property;

    public RequiredPropertyNotFoundException(String key, String... localizers) {
        super("Cannot find property \"" + key + "\"", localizers);
        this.property = key;
    }
}
