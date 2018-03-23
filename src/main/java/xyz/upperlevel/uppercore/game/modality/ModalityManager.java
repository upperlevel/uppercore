package xyz.upperlevel.uppercore.game.modality;

import java.util.HashMap;
import java.util.Map;

public class ModalityManager {
    private Map<String, ModalityFactory> byId = new HashMap<>();

    public ModalityManager() {
        register(BungeeModality.factory());
        register(MultiArenaModality.factory());
    }

    public void register(ModalityFactory factory) {
        byId.put(factory.getId(), factory);
    }

    public ModalityFactory get(String id) {
        return byId.get(id);
    }
}
