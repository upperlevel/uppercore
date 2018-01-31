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
            if (child == null) {
                return parent;
            }
            if (parent == null) {
                return child;
            }
            StringJoiner path = new StringJoiner(".");
            path.add(parent.getName());
            path.add(child.getName());

            Permission result = new Permission(path.toString(), child.getDescription(), child.getDefault());
            result.addParent(parent, true);
            return result;
        }
    };

    public abstract Permission complete(Permission parent, Permission child);
}
