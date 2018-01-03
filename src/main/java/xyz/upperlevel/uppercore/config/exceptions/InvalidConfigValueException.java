package xyz.upperlevel.uppercore.config.exceptions;

import lombok.Getter;

@Getter
public class InvalidConfigValueException extends InvalidConfigException {
    private final String key;
    private final Object value;
    private final String expected;

    public InvalidConfigValueException(String key, Object value, String expected, String... locations) {
        super("Invalid value in \"" + key + "\" (found \"" + value.getClass().getSimpleName() + "\", expected \"" + expected + "\")", locations);
        this.key = key;
        this.value = value;
        this.expected = expected;
    }
}
