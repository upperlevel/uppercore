package xyz.upperlevel.uppercore.gui;

import xyz.upperlevel.uppercore.gui.link.Link;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiAction {

    public static Link close() {
        return guis()::close;
    }

    public static Link back() {
        return guis()::back;
    }

    public static Link change(Gui gui) {
        return p -> guis().change(p, gui);
    }


    public static Link add(Link... links) {
        return p -> {
            for (Link link : links)
                link.run(p);
        };
    }
}
