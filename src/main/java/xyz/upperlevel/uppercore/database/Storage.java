package xyz.upperlevel.uppercore.database;

public interface Storage {
    String getId();

    Connection connect(String database);

    Connection connect(String database, String host, int port);

    Connection connect(String database, String host, int port, String user, String password);

    boolean isSupported();

    String[] getDownloadLinks();
}
