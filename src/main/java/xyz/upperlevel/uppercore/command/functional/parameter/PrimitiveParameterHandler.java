package xyz.upperlevel.uppercore.command.functional.parameter;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;

public final class PrimitiveParameterHandler {
    private PrimitiveParameterHandler() {
    }

    public static void register() {
        // Boolean
        ParameterHandler.register(
                Arrays.asList(boolean.class, Boolean.class),
                args -> {
                    String arg = args.take();
                    return Arrays.asList("true", "t", "yes", "y", "1").contains(arg);
                });

        // Char
        ParameterHandler.register(
                Arrays.asList(char.class, Character.class),
                args -> {
                    String arg = args.take();
                    if (arg.length() > 1)
                        throw args.areWrong();
                    return arg.charAt(0);
                });

        // Float
        ParameterHandler.register(
                Arrays.asList(float.class, Float.class),
                args -> {
                    String arg = args.take();
                    try {
                        return Float.parseFloat(arg);
                    } catch (NumberFormatException ign) {
                        throw args.areWrong();
                    }
                });

        // Double
        ParameterHandler.register(
                Arrays.asList(double.class, Double.class),
                args -> {
                    String arg = args.take();
                    try {
                        return Double.parseDouble(arg);
                    } catch (NumberFormatException ign) {
                        throw args.areWrong();
                    }
                });

        // Byte
        ParameterHandler.register(
                Arrays.asList(byte.class, Byte.class),
                args -> {
                    String arg = args.take();
                    try {
                        return Byte.parseByte(arg);
                    } catch (NumberFormatException ign) {
                        throw args.areWrong();
                    }
                });

        // Int
        ParameterHandler.register(
                Arrays.asList(int.class, Integer.class),
                args -> {
                    String arg = args.take();
                    try {
                        return Integer.parseInt(arg);
                    } catch (NumberFormatException ign) {
                        throw args.areWrong();
                    }
                });

        // Long
        ParameterHandler.register(
                Arrays.asList(long.class, Long.class),
                args -> {
                    String arg = args.take();
                    try {
                        return Long.parseLong(arg);
                    } catch (NumberFormatException ign) {
                        throw args.areWrong();
                    }
                });

        // String
        ParameterHandler.register(
                Collections.singletonList(String.class),
                ParameterHandler.ArgsTracker::take
        );

        // String[]
        ParameterHandler.register(
                Collections.singletonList(String[].class),
                args -> args.takeAll().toArray(new String[0])
        );
    }
}
