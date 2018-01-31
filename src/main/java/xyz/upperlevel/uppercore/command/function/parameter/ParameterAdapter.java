package xyz.upperlevel.uppercore.command.function.parameter;

import java.util.List;

public interface ParameterAdapter {
    /**
     * Gets the types that this adapter can adapt.
     */
    List<Class<?>> getTypes();

    /**
     * Gets the number of arguments needed to adapt the parameter.
     * It will be the argument count used in {@link #adapt(List)}.
     */
    int getConsumeCount(); // -1 if unlimited

    /**
     * Adapts the needed argument count from passed ones to the type.
     */
    Object adapt(List<String> arguments) throws ParameterParseException;

    List<String> suggest(List<String> arguments);
}
