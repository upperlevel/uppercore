package xyz.upperlevel.uppercore.gui;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.gui.link.Link;

public class GuiAction {

    public static Link close() {
        return GuiManager::closeGui;
    }

    public static Link back() {
        return GuiManager::backGui;
    }

    public static Link change(Gui gui) {
        return p -> GuiManager.changeGui(p, gui);
    }


    public static Link add(Link... links) {
        return p -> {
            for (Link link : links)
                link.run(p);
        };
    }
}
