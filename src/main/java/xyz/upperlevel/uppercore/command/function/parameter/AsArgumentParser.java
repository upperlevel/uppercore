package xyz.upperlevel.uppercore.command.function.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AsArgumentParser {
    /**
     * The types that can parse.
     */
    Class<?>[] parsableTypes();

    /**
     * The number of arguments that uses.
     */
    int consumeCount();
}
