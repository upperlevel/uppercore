package xyz.upperlevel.uppercore.placeholder.message;

import lombok.Getter;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

@Getter
public class IllegalMessageConfigException extends IllegalStateException {
    private final String path;
    private final String messagePath;

    public IllegalMessageConfigException(String path, String message) {
        super("Cannot find message '" + (path.isEmpty() ? "" : path + '.') + message + "'!");
        this.path = path;
        this.messagePath = message;
    }

    public IllegalMessageConfigException(String path) {
        super("Cannot find message section '" + path + "'!");
        this.path = path;
        this.messagePath = null;
    }

    public IllegalMessageConfigException(String path, String message, InvalidConfigException e) {
        super("Error in message '" + (path.isEmpty() ? "" : path + '.') + message + "': " + e.getConfigError());
        this.path = path;
        this.messagePath = message;
    }

    public IllegalMessageConfigException(String path, InvalidConfigException e) {
        super("Error in message section '" + path + "': " + e.getConfigError());
        this.path = path;
        this.messagePath = null;
    }
}
