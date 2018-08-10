package xyz.upperlevel.uppercore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines the constructor for a class.
 * <br> This can be declared both on a method or a constructor but with two different meanings:
 * <br> In the constructor it means that the current class can be Config-parsed using that constructor,
 * <br> In a method it means that the method can be used to parse the class returned by it
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigConstructor {
    boolean inlineable() default false;
}
