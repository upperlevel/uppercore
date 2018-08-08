package xyz.upperlevel.uppercore.command.functional.parameter;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionalArgumentParser implements ArgumentParser {
    @Getter
    private Object residence;

    @Getter
    private AsArgumentParser annotation;

    @Getter
    private Method function;

    public FunctionalArgumentParser(Object residence, AsArgumentParser annotation, Method function) {
        this.residence = residence;
        this.annotation = annotation;
        this.function = function;
    }

    @Override
    public Class<?>[] getParsableTypes() {
        return annotation.parsableTypes();
    }

    @Override
    public int getConsumedCount() {
        return annotation.consumeCount();
    }

    @Override
    public Object parse(List<String> args) throws ArgumentParseException {
        try {
            return function.invoke(residence, args);
        } catch (IllegalAccessException ignored) {
            // impossible
            throw new IllegalStateException();
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ArgumentParseException) {
                throw (ArgumentParseException) e.getCause();
            } else {
                throw new IllegalStateException("An error occurred in ArgumentParser function: " + function.getName(), e);
            }
        }
    }

    @Override
    public List<String> suggest(List<String> arguments) {
        // functional argument parsers not support suggestions
        return Collections.emptyList();
    }

    public static List<ArgumentParser> load(Object residence) {
        List<ArgumentParser> result = new ArrayList<>();
        for (Method function : residence.getClass().getMethods()) {
            AsArgumentParser annotation = function.getAnnotation(AsArgumentParser.class);
            if (annotation != null) {
                result.add(new FunctionalArgumentParser(residence, annotation, function));
            }
        }
        return result;
    }
}
