package xyz.upperlevel.uppercore.database;

public interface DatabaseDriver {
    String getId();

    Connection connect(String database);

    Connection connect(String database, String host, int port);

    Connection connect(String database, String host, int port, String user, String password);
}
