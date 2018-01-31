package xyz.upperlevel.uppercore.command.function.parameter;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ParameterParseException extends Exception {
    @Getter
    private Class<?> parameterType;

    @Getter
    private List<String> arguments;

    public ParameterParseException(Class<?> parameterType, List<String> arguments) {
        this.parameterType = parameterType;
        this.arguments = arguments;
    }

    public String getDefaultMessage() {
        return StringUtils.join(arguments, " ") + " cannot be parsed to: " + parameterType.getSimpleName();
    }
}
