package xyz.upperlevel.uppercore.storage;

import java.util.Map;

public interface Element {
    Object get(String parameter);

    Map<String, Object> getAll();

    void update(Map<String, Object> data);

    void drop();
}
