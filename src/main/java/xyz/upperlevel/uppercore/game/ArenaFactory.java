package xyz.upperlevel.uppercore.game;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface ArenaFactory {
    Arena create(String id);

    default Arena load(String id, Map<String, Object> data) {
        Arena arena = create(id);
        arena.deserialize(data);
        return arena;
    }

    static ArenaFactory fromClass(Class<? extends Arena> arenaClass) {
        Constructor<? extends Arena> creator;
        try {
            creator = arenaClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("An Arena class loaded in this way must have a constructor with one String parameter.", e);
        }
        return id -> {
            try {
                return creator.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Arena constructor was invalid.", e);
            }
        };
    }
}
