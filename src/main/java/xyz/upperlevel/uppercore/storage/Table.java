package xyz.upperlevel.uppercore.storage;

import java.util.List;

public interface Table {
    boolean create();

    boolean drop();

    Element element(String id);
}
