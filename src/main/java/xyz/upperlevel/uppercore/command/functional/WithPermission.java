package xyz.upperlevel.uppercore.command.functional;

import xyz.upperlevel.uppercore.command.PermissionUser;
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

    PermissionUser user() default PermissionUser.OP;

    PermissionCompleter completer() default PermissionCompleter.INHERIT;
}
