package xyz.upperlevel.uppercore.command.functional;

import xyz.upperlevel.uppercore.command.DefaultPermissionUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithEveryPermission {
    String description();

    DefaultPermissionUser defaultUser() default DefaultPermissionUser.INHERIT;
}
