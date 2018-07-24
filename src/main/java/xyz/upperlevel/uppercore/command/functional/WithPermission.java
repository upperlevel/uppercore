package xyz.upperlevel.uppercore.command.functional;

import xyz.upperlevel.uppercore.command.DefaultPermissionUser;
import xyz.upperlevel.uppercore.command.PermissionCompleter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
public @interface WithPermission {
    String value() default "";

    String description() default "";

    DefaultPermissionUser defaultUser() default DefaultPermissionUser.INHERIT;

    PermissionCompleter completer() default PermissionCompleter.INHERIT;
}
