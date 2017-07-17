package xyz.upperlevel.uppercore.gui;

import xyz.upperlevel.uppercore.gui.link.Link;

public class GuiAction {

    public static Link close() {
        return GuiSystem::close;
    }

    public static Link back() {
        return GuiSystem::back;
    }

    public static Link change(Gui gui) {
        return p -> GuiSystem.change(p, gui);
    }


    public static Link add(Link... links) {
        return p -> {
            for (Link link : links)
                link.run(p);
        };
    }
}
