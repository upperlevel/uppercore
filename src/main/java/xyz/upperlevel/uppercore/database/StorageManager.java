package xyz.upperlevel.uppercore.database;

import xyz.upperlevel.uppercore.database.impl.Flatfile;
import xyz.upperlevel.uppercore.database.impl.MongoDb;
import xyz.upperlevel.uppercore.database.impl.MySql;
import xyz.upperlevel.uppercore.database.impl.RethinkDb;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StorageManager {
    private final Map<String, Storage> storageByName = new HashMap<>();

    public StorageManager() {
        registerStorage(new Flatfile());
        registerStorage(new MongoDb());
        registerStorage(new RethinkDb());
        registerStorage(new MySql());
    }

    public void registerStorage(Storage storage) {
        storageByName.put(storage.getName().toLowerCase(Locale.ENGLISH), storage);
    }

    public Storage getStorage(String name) {
        return storageByName.get(name.toLowerCase(Locale.ENGLISH));
    }
}