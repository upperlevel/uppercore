package xyz.upperlevel.uppercore.database;

import xyz.upperlevel.uppercore.database.impl.Flatfile;
import xyz.upperlevel.uppercore.database.impl.MongoDb;
import xyz.upperlevel.uppercore.database.impl.MySql;
import xyz.upperlevel.uppercore.database.impl.RethinkDb;

import java.util.HashMap;
import java.util.Map;

public class DatabaseDriverManager {
    private final Map<String, DatabaseDriver> drivers = new HashMap<>();

    public DatabaseDriverManager() {
        register(new Flatfile());
        register(new MongoDb());
        register(new MySql());
        register(new RethinkDb());
    }

    public void register(DatabaseDriver driver) {
        drivers.put(driver.getId(), driver);
    }

    public DatabaseDriver get(String id) {
        return drivers.get(id);
    }

    public Flatfile flatfile() {
        return (Flatfile) get("flatfile");
    }

    public MongoDb mongodb() {
        return (MongoDb) get("mongodb");
    }

    public MySql mysql() {
        return (MySql) get("mysql");
    }

    public RethinkDb rethinkdb() {
        return (RethinkDb) get("rethinkdb");
    }
}