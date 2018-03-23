package xyz.upperlevel.uppercore.game.modality;

import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.game.arena.ArenaManager;

public interface ModalityFactory {
    String getId();

    Modality load(ArenaManager arenaManager, Config config);
}
