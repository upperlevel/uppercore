package xyz.upperlevel.uppercore.database;

import lombok.Getter;
import xyz.upperlevel.uppercore.config.Config;

public class StorageLocation {
    @Getter
    private String address;

    @Getter
    private int port;

    @Getter
    private String database;

    // Non gettable (just to have a little security)
    private String username, password;

    private StorageLocation() {
    }

    /**
     * Sets the address of the storage.
     */
    public StorageLocation address(String address) {
        this.address = address;
        return this;
    }

    /**
     * Sets the port of the storage.
     */
    public StorageLocation port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the database name of the storage.
     * The one that we want to connect to.
     */
    public StorageLocation database(String database) {
        this.database = database;
        return this;
    }

    /**
     * Sets the username of the storage.
     */
    public StorageLocation username(String username) {
        this.username = username;
        return this;
    }

    /**
     * Sets the password of the storage.
     */
    public StorageLocation password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Connects to the given {@link Storage} with current parameters:
     * address, port, database, username and password.
     *
     * @return a {@link Database} where is possible to write and read.
     */
    public Database connect(Storage storage) {
        return storage.connect(address, port, database, username, password);
    }

    /**
     * Creates an empty {@link StorageLocation}.
     */
    public static StorageLocation create() {
        return new StorageLocation();
    }

    /**
     * Loads the {@link StorageLocation} from a configuration.
     */
    public static StorageLocation load(Config config) {
        StorageLocation r = new StorageLocation();
        r.address = config.getString("address");
        r.port = config.getInt("port");
        r.database = config.getString("database");
        r.username = config.getString("username");
        r.password = config.getString("password");
        return r;
    }
}
