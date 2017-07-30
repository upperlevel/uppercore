package xyz.upperlevel.uppercore.database;

public interface Accessor {
    void authenticate(Credential credential);

    default void authenticate(String database, String username, String password) {
        authenticate(new Credential(database, username, password));
    }

    Connection connect();

    Connection connect(String host, int port);
}
