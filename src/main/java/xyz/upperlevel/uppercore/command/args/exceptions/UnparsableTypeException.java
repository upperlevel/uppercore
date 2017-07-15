package xyz.upperlevel.uppercore.command.args.exceptions;

public class UnparsableTypeException extends RuntimeException {

    public UnparsableTypeException(Class<?> type) {
        this(type.getName());
    }

    public UnparsableTypeException(String message) {
        super(message);
    }
}
