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
    /**
     * When true the object will be parsed even when in an array form,
     * this is desirable for objects as vectors as they are mostly written as [x, y, z] instead of a map.
     * This could also be used when a constructor has only one type as argument, then it would not need an
     * array to be constructed.
     * @return
     */
    boolean inlineable() default false;
}
