package xyz.upperlevel.uppercore.database;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.database.flatfile.Flatfile;
import xyz.upperlevel.uppercore.database.flatfile.FlatfileDriver;
import xyz.upperlevel.uppercore.database.mongodb.MongoDb;
import xyz.upperlevel.uppercore.database.mongodb.MongoDbDriver;
import xyz.upperlevel.uppercore.database.mysql.MySql;
import xyz.upperlevel.uppercore.database.mysql.MySqlDriver;
import xyz.upperlevel.uppercore.database.rethinkdb.RethinkDb;
import xyz.upperlevel.uppercore.database.rethinkdb.RethinkDbDriver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {
    private final Map<String, Driver> drivers = new HashMap<>();
    private final Config config;

    public DriverManager() {
        Uppercore.get().saveResource("db_sources.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(Uppercore.get().getDataFolder(), "db_sources.yml"))::get;

        // registers default supported drivers
        register(new FlatfileDriver());
        register(new MongoDbDriver());
        register(new MySqlDriver());
        register(new RethinkDbDriver());
    }

    public void register(Driver driver) {
        String id = driver.getId();
        drivers.put(id, driver);
        if (config.has(id))
            driver.load(config.getConfig(id));
    }

    public Driver get(String id) {
        return drivers.get(id);
    }

    public FlatfileDriver flatfile() {
        return (FlatfileDriver) get("flatfile");
    }

    public MongoDbDriver mongodb() {
        return (MongoDbDriver) get("mongodb");
    }

    public MySqlDriver mysql() {
        return (MySqlDriver) get("mysql");
    }

    public RethinkDbDriver rethinkdb() {
        return (RethinkDbDriver) get("rethinkdb");
    }
}