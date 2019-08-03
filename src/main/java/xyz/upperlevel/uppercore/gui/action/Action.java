package xyz.upperlevel.uppercore.gui.action;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.PolymorphicSelector;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.gui.link.Link;
import xyz.upperlevel.uppercore.registry.Registry;

public abstract class Action<T extends Action<T>> implements Link {
    @Getter
    private final ActionType<T> type;

    @Getter
    @Setter
    private Registry<?> registry;

    public Action(ActionType<T> type) {
        this.type = type;
        this.registry = Uppercore.registry();
    }

    @PolymorphicSelector
    private static Class<? extends Action> selectAction(@ConfigProperty("type") String rawType) {
        ActionType<?> type = ActionType.getActionType(rawType);
        if (type == null) {
            throw new InvalidConfigException("Cannot find action " + rawType);
        }
        return type.getHandleClass();
    }
}
