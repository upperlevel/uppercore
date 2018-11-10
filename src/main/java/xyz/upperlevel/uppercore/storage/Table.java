package xyz.upperlevel.uppercore.storage;

public interface Table {
    boolean create();

    boolean drop();

    Element element(String id);
}
