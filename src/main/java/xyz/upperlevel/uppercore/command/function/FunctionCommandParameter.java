package xyz.upperlevel.uppercore.command.function;

import lombok.Getter;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.command.CommandParameter;

import java.lang.reflect.Parameter;
import java.util.StringJoiner;

public class FunctionCommandParameter extends CommandParameter {
    @Getter
    private Parameter parameter;

    public FunctionCommandParameter(FunctionCommand command, Parameter parameter) {
        super(command);
        WithName nameAnnotation = parameter.getAnnotation(WithName.class);
        if (nameAnnotation != null) {
            setName(nameAnnotation.value());
        }
        WithOptional optionalAnnotation = parameter.getAnnotation(WithOptional.class);
        if (optionalAnnotation != null) {
            setOptional(true);
            setDefaultValue(optionalAnnotation.value());
        }
        WithPermission permissionAnnotation = parameter.getAnnotation(WithPermission.class);
        if (permissionAnnotation != null) {
            StringJoiner path = new StringJoiner(".");
            if (command.getPermission() != null) {
                path.add(command.getPermission().getName());
            }
            path.add(permissionAnnotation.value());
            Permission permission = new Permission(path.toString());
            if (command.getPermission() != null) {
                permission.addParent(command.getPermission(), true);
            }
            permission.setDescription(permissionAnnotation.value());
            setPermission(permission);
        }
        this.parameter = parameter;
    }


}
