package xyz.upperlevel.uppercore.database;

import xyz.upperlevel.uppercore.database.impl.Flatfile;
import xyz.upperlevel.uppercore.database.impl.MongoDb;
import xyz.upperlevel.uppercore.database.impl.MySql;
import xyz.upperlevel.uppercore.database.impl.RethinkDb;

import java.util.HashMap;
import java.util.Map;

public class StorageManager {
    private final Map<String, Storage> storages = new HashMap<>();

    /**
     * Instantiate a new storage manager and registers default storages.
     */
    public StorageManager() {
        register(new Flatfile());
        register(new MongoDb());
        register(new RethinkDb());
        register(new MySql());
    }

    /**
     * Registers a storage.
     */
    public void register(Storage storage) {
        storages.put(storage.getId(), storage);
    }

    /**
     * Gets a registered storage by id.
     */
    public Storage get(String id) {
        return storages.get(id);
    }

    public Flatfile flatfile() {
        return (Flatfile) get("flatfile");
    }

    public MongoDb mongodb() {
        return (MongoDb) get("mongodb");
    }

    public RethinkDb rethinkdb() {
        return (RethinkDb) get("rethinkdb");
    }

    public MySql mysql() {
        return (MySql) get("mysql");
    }
}