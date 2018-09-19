package xyz.upperlevel.uppercore.storage;

public interface Database {
    @Deprecated
    boolean create();

    boolean drop();

    Table table(String name);
}
