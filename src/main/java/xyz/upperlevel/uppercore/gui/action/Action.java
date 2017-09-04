package xyz.upperlevel.uppercore.gui.action;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.link.Link;

@Data
public abstract class Action<T extends Action<T>> implements Link {

    private final Plugin plugin;
    private final ActionType<T> type;
}
