package xyz.upperlevel.uppercore.config.exceptions;

public class UnparsableConfigClass extends RuntimeException {
    public UnparsableConfigClass(Class<?> clazz) {
        super("Cannot find parser for class " + clazz.getName());
    }
}
