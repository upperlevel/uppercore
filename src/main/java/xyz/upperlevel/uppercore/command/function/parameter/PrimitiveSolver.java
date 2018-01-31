package xyz.upperlevel.uppercore.command.function.parameter;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

public class PrimitiveSolver {
    public PrimitiveSolver() {
    }

    public Boolean parseBoolean(List<String> arguments) throws ParameterParseException {
        String value = arguments.get(0);
        switch (value) {
            case "false":
            case "f":
            case "0":
                return false;
            case "true":
            case "t":
            case "1":
                return true;
            default:
                throw new ParameterParseException(boolean.class, Collections.singletonList(arguments.get(0)));
        }
    }

    public Float parseFloat(List<String> arguments) throws ParameterParseException {
        try {
            return Float.parseFloat(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ParameterParseException(float.class, Collections.singletonList(arguments.get(0)));
        }
    }

    public Double parseDouble(List<String> arguments) throws ParameterParseException {
        try {
            return Double.parseDouble(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ParameterParseException(double.class, Collections.singletonList(arguments.get(0)));
        }
    }

    public Integer parseInteger(List<String> arguments) throws ParameterParseException {
        try {
            return Integer.parseInt(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ParameterParseException(int.class, Collections.singletonList(arguments.get(0)));
        }
    }

    public Long parseLong(List<String> arguments) throws ParameterParseException {
        try {
            return Long.parseLong(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ParameterParseException(long.class, Collections.singletonList(arguments.get(0)));
        }
    }

    public char parseCharacter(List<String> arguments) throws ParameterParseException {
        return arguments.get(0).charAt(0);
    }

    public String parseString(List<String> arguments) throws ParameterParseException {
        return StringUtils.join(arguments, " ");
    }
}
