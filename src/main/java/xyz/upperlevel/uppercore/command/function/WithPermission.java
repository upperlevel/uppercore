package xyz.upperlevel.uppercore.command.function;

import xyz.upperlevel.uppercore.command.DefaultPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithPermission {
    String value();

    String description() default "";

    DefaultPermission defaultPermission() default DefaultPermission.INHERIT;
}
