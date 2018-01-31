package xyz.upperlevel.uppercore.command.function.parameter;

import lombok.Getter;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.command.CommandParameter;
import xyz.upperlevel.uppercore.command.function.*;

import java.lang.reflect.Parameter;

public class FunctionalParameter extends CommandParameter {
    @Getter
    private Parameter parameter;

    @Getter
    private ParameterAdapter solver;

    public FunctionalParameter(Parameter parameter, ParameterAdapter solver, FunctionalCommand command) {
        super(command);
        this.parameter = parameter;
        this.solver = solver;
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
            setRelativePermission(new Permission(permission.value(), permission.description(), permission.defaultUser().get(command)));
            setPermissionCompleter(permission.completer());
        }
    }
}
