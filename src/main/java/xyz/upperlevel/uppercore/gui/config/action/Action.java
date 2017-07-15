package xyz.upperlevel.uppercore.gui.config.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.uppercore.gui.link.Link;

@RequiredArgsConstructor
public abstract class Action<T extends Action<T>> implements Link {
    @Getter
    private final ActionType<T> type;
}
