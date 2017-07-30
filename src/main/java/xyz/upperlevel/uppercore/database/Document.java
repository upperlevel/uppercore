package xyz.upperlevel.uppercore.database;

import java.util.Map;

public interface Document {
    String getId();

    Map<String, Object> ask();

    void send(Map<String, Object> data);
}
