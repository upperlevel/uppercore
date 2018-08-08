package xyz.upperlevel.uppercore.command;

import org.bukkit.permissions.PermissionDefault;

public enum DefaultPermissionUser {
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
            return (parent == null || parent.getPermissionPortion() == null) ? PermissionDefault.TRUE : parent.getPermissionPortion().getDefault();
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
}
