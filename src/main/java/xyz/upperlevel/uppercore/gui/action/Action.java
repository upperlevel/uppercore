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
import xyz.upperlevel.uppercore.registry.RegistryTraceable;

public abstract class Action<T extends Action<T>> implements Link, RegistryTraceable {
    @Getter
    private final ActionType<T> type;

    @Getter
    @Setter
    private Registry registry = Uppercore.registry();

    @Override
    public void setParentRegistry(Registry registry) {
        setRegistry(registry);
    }

    public Action(ActionType<T> type) {
        this.type = type;
    }
}
