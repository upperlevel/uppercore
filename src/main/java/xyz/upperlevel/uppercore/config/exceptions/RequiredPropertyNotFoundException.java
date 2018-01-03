package xyz.upperlevel.uppercore.config.exceptions;

import lombok.Getter;

@Getter
public class RequiredPropertyNotFoundException extends InvalidConfigException {
    private final String property;

    public RequiredPropertyNotFoundException(String key, String... locators) {
        super("Cannot find property \"" + key + "\"", locators);
        this.property = key;
    }
}
