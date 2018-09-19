package xyz.upperlevel.uppercore.command;

import org.bukkit.permissions.PermissionDefault;

public enum PermissionUser {
    AVAILABLE(PermissionDefault.TRUE),
    UNAVAILABLE(PermissionDefault.FALSE),
    NOT_OP(PermissionDefault.NOT_OP),
    OP(PermissionDefault.OP);

    private final PermissionDefault wrapped;

    PermissionUser(PermissionDefault wrapped) {
        this.wrapped = wrapped;
    }

    public PermissionDefault get() {
        return wrapped;
    }
}
