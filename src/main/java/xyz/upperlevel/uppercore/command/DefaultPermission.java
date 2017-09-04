package xyz.upperlevel.uppercore.command;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum DefaultPermission {
    TRUE {
        @Override
        public PermissionDefault get(Command command) {
            return PermissionDefault.TRUE;
        }
    },
    FALSE {
        @Override
        public PermissionDefault get(Command command) {
            return PermissionDefault.FALSE;
        }
    },
    INHERIT {
        @Override
        public PermissionDefault get(Command command) {
            Command parent = command.getParent();
            return (parent == null || parent.getPermission() == null) ? PermissionDefault.TRUE : parent.getPermission().getDefault();
        }
    },
    NOT_OP {
        @Override
        public PermissionDefault get(Command command) {
            return PermissionDefault.NOT_OP;
        }
    },
    OP {
        @Override
        public PermissionDefault get(Command command) {
            return PermissionDefault.OP;
        }
    };

    public abstract PermissionDefault get(Command command);

    public void apply(Command command, Permission perm) {
        perm.setDefault(get(command));
    }
}
