package xyz.upperlevel.uppercore.database;

public interface Collection {
    String getId();

    Document document(String id);
}
