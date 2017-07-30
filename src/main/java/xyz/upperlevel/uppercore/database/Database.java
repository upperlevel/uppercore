package xyz.upperlevel.uppercore.database;

public interface Database {
    String getId();

    Collection collection(String id);
}
