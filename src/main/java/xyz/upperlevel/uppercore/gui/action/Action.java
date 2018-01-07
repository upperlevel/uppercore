package xyz.upperlevel.uppercore.gui.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.link.Link;

@RequiredArgsConstructor
public abstract class Action<T extends Action<T>> implements Link {
    @Getter
    private final Plugin plugin;
    @Getter
    private final ActionType<T> type;
}
