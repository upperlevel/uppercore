package xyz.upperlevel.uppercore.config.exceptions;

public class ConfigParsingException extends RuntimeException {
    public ConfigParsingException(Object value, Exception e) {
        super("Cannot parse '" + value + "'", e);
    }

    public ConfigParsingException(Object value) {
        super("Cannot parse '" + value + "'");
    }
}
