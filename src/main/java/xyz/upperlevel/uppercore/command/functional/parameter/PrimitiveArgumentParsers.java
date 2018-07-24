package xyz.upperlevel.uppercore.command.functional.parameter;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PrimitiveArgumentParsers {
    public PrimitiveArgumentParsers() {
    }

    // Boolean
    @AsArgumentParser(
            parsableTypes = {Boolean.class, boolean.class},
            consumeCount = 1
    )
    public Boolean parseBoolean(List<String> arguments) throws ArgumentParseException {
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
                throw new ArgumentParseException(boolean.class, Collections.singletonList(arguments.get(0)));
        }
    }

    // Float
    @AsArgumentParser(
            parsableTypes = {Float.class, float.class},
            consumeCount = 1
    )
    public Float parseFloat(List<String> arguments) throws ArgumentParseException {
        try {
            return Float.parseFloat(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(float.class, Collections.singletonList(arguments.get(0)));
        }
    }

    // Double
    @AsArgumentParser(
            parsableTypes = {Double.class, double.class},
            consumeCount = 1
    )
    public Double parseDouble(List<String> arguments) throws ArgumentParseException {
        try {
            return Double.parseDouble(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(double.class, Collections.singletonList(arguments.get(0)));
        }
    }

    // Integer
    @AsArgumentParser(
            parsableTypes = {Integer.class, int.class},
            consumeCount = 1
    )
    public Integer parseInteger(List<String> arguments) throws ArgumentParseException {
        try {
            return Integer.parseInt(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(int.class, Collections.singletonList(arguments.get(0)));
        }
    }

    // Long
    @AsArgumentParser(
            parsableTypes = {Long.class, long.class},
            consumeCount = 1
    )
    public Long parseLong(List<String> arguments) throws ArgumentParseException {
        try {
            return Long.parseLong(arguments.get(0));
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(long.class, Collections.singletonList(arguments.get(0)));
        }
    }

    // Character
    @AsArgumentParser(
            parsableTypes = {Character.class, char.class},
            consumeCount = 1

    )
    public char parseCharacter(List<String> arguments) throws ArgumentParseException {
        return arguments.get(0).charAt(0);
    }

    // String
    @AsArgumentParser(
            parsableTypes = {String.class},
            consumeCount = 1
    )
    public String parseString(List<String> arguments) throws ArgumentParseException {
        return StringUtils.join(arguments, " ");
    }

    // Array
    @AsArgumentParser(
            parsableTypes = {String[].class},
            consumeCount = -1
    )
    public String[] parseArray(List<String> args) throws ArgumentParseException {
        return args.toArray(new String[0]);
    }
}
