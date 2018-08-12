package xyz.upperlevel.uppercore.storage;

import xyz.upperlevel.uppercore.storage.impl.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StorageManager {
    private final Map<String, StorageConnector> storageByName = new HashMap<>();

    public StorageManager() {
        // SQL
        register(MariaDb.storage());
        register(MySql.storage());

        // NoSQL
        register(MongoDb.storage());
        register(RethinkDb.storage());
        register(NitriteDb.storage());
        register(Flatfile.storage());
    }

    public void register(StorageConnector storage) {
        storageByName.put(storage.getName().toLowerCase(Locale.ENGLISH), storage);
    }

    public StorageConnector get(String name) {
        return storageByName.get(name.toLowerCase(Locale.ENGLISH));
    }
}