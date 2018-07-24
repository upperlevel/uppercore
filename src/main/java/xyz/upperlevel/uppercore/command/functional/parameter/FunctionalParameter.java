package xyz.upperlevel.uppercore.command.functional.parameter;

import lombok.Getter;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.command.CommandParameter;
import xyz.upperlevel.uppercore.command.functional.*;

import java.lang.reflect.Parameter;

public class FunctionalParameter extends CommandParameter {
    @Getter
    private Parameter parameter;

    @Getter
    private ArgumentParser parser;

    public FunctionalParameter(Parameter parameter, ArgumentParser parser, FunctionalCommand command) {
        super(command);
        this.parameter = parameter;
        this.parser = parser;
        WithName name = parameter.getAnnotation(WithName.class);
        if (name != null) {
            setName(name.value());
        }
        WithOptional optional = parameter.getAnnotation(WithOptional.class);
        if (optional != null) {
            setOptional(true);
            setDefaultValue(optional.value());
        }
        WithPermission permission = parameter.getAnnotation(WithPermission.class);
        if (permission != null) {
            setPermissionPortion(new Permission(permission.value(), permission.description(), permission.defaultUser().get(command)));
            setPermissionCompleter(permission.completer());
        }
    }
}
