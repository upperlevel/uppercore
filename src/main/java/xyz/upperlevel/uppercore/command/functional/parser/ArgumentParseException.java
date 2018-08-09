package xyz.upperlevel.uppercore.command.functional.parser;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ArgumentParseException extends Exception {
    @Getter
    private Class<?> parameterType;

    @Getter
    private List<String> arguments;

    public ArgumentParseException(Class<?> parameterType, List<String> arguments) {
        this.parameterType = parameterType;
        this.arguments = arguments;
    }

    public String getDefaultMessage() {
        return StringUtils.join(arguments, " ") + " cannot be parsed to: " + parameterType.getSimpleName();
    }
}
