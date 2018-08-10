package xyz.upperlevel.uppercore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to declare the loading priority of the declarator in a ConfigExternalDeclarator class.
 * The methods with an higher priority will be loaded before the others.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalDeclaratorPriority {
    int value() default 100;
}
