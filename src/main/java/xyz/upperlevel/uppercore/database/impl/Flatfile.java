package xyz.upperlevel.uppercore.database.impl;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.upperlevel.uppercore.database.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Flatfile implements DatabaseDriver {
    @Override
    public String getId() {
        return "flatfile";
    }

    @Override
    public Connection connect(String database) {
        return new ImplConnection(database);
    }

    @Override
    public Connection connect(String database, String host, int port) {
        return new ImplConnection(database);
    }

    @Override
    public Connection connect(String database, String host, int port, String user, String password) {
        return new ImplConnection(database);
    }

    @Data
    public class ImplConnection implements Connection {
        private final String db;

        @Override
        public ImplDatabase database() {
            return new ImplDatabase();
        }

        @Data
        public class ImplDatabase implements Database {
            private final File folder;

            public ImplDatabase() {
                this.folder = new File("plugins", db);
                this.folder.mkdirs();
            }

            @Override
            public Table table(String id) {
                return new ImplTable(id);
            }

            @Data
            public class ImplTable implements Table {
                private final File folder;

                public ImplTable(String id) {
                    this.folder = new File(ImplDatabase.this.folder, id);
                    this.folder.mkdirs();
                }

                @Override
                public Document document(String id) {
                    return new ImplDocument(id);
                }

                @Data
                public class ImplDocument implements Document {
                    private final File file;

                    public ImplDocument(String id) {
                        this.file = new File(ImplTable.this.getFolder(), id + ".yml");
                        try {
                            this.file.createNewFile();
                        } catch (IOException ignored) {
                        }
                    }

                    @Override
                    public Map<String, Object> ask() {
                        return YamlConfiguration.loadConfiguration(file).getValues(false);
                    }

                    @Override
                    public void send(Map<String, Object> data) {
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                        for (String key : data.keySet())
                            cfg.set(key, cfg.get(key));
                        try {
                            cfg.save(file);
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
    }
}
