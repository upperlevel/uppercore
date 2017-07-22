package xyz.upperlevel.uppercore.placeholder;

import org.bukkit.entity.Player;

public interface Placeholder {

    String getId();

    String resolve(Player player, String arg);

    static Placeholder constant(String obj) {
        return new Placeholder() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String resolve(Player player, String arg) {
                return obj;
            }
        };
    }
}
