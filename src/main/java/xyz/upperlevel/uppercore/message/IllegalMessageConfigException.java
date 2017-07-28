package xyz.upperlevel.uppercore.message;

import lombok.Getter;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigurationException;

@Getter
public class IllegalMessageConfigException extends IllegalStateException {
    private final String path;
    private final String messagePath;

    public IllegalMessageConfigException(String path, String message) {
        super("Cannot find message '" + path + '.' + message + "'!");
        this.path = path;
        this.messagePath = message;
    }

    public IllegalMessageConfigException(String path) {
        super("Cannot find section '" + path + "'!");
        this.path = path;
        this.messagePath = null;
    }

    public IllegalMessageConfigException(String path, String message, InvalidConfigurationException e) {
        super("Error in '" + path + '.' + message + "': " + e.getConfigError());
        this.path = path;
        this.messagePath = message;
    }

    public IllegalMessageConfigException(String path, InvalidConfigurationException e) {
        super("Error in section '" + path + "': " + e.getConfigError());
        this.path = path;
        this.messagePath = null;
    }
}
