package xyz.upperlevel.uppercore.command;

import org.bukkit.permissions.Permission;

import java.util.StringJoiner;

public enum PermissionCompleter {
    NONE {
        @Override
        public Permission complete(Permission parent, Permission child) {
            return child;
        }
    },
    INHERIT {
        @Override
        public Permission complete(Permission parent, Permission child) {
            StringJoiner path = new StringJoiner(".");
            path.add(parent.getName());
            path.add(child.getName());
            return new Permission(path.toString(), child.getDescription(), child.getDefault());
        }
    };

    /**
     * Completes the permission based on the modality chosen.
     * Expects that both parent and child permissions are not null.
     */
    public abstract Permission complete(Permission parent, Permission child);

}
