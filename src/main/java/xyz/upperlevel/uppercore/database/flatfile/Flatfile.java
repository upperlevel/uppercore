package xyz.upperlevel.uppercore.database.flatfile;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.upperlevel.uppercore.database.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Flatfile implements Accessor {
    @Override
    public void authenticate(Credential credential) {
    }

    @Override
    public Connection connect() {
        return connect(null, -1);
    }

    @Override
    public Conn connect(String host, int port) {
        return new Conn();
    }

    @Data
    public class Conn implements Connection {
        @Override
        public Database database(String id) {
            return null;
        }

        @Override
        public void disconnect() {
        }

        @Data
        public class Db implements Database {
            private final String id;
            private final File folder;

            public Db(String id) {
                this.id = id;
                this.folder = new File("plugins", id);
            }

            @Override
            public Coll collection(String id) {
                folder.mkdirs();
                return new Coll(id);
            }

            @Data
            public class Coll implements Collection {
                private final String id;
                private final File folder;

                public Coll(String id) {
                    this.id = id;
                    this.folder = new File(Db.this.folder, id);
                }

                @Override
                public Doc document(String id) {
                    folder.mkdirs();
                    return new Doc(id);
                }

                @Data
                public class Doc implements xyz.upperlevel.uppercore.database.Document {
                    private final String id;
                    private final File file;

                    public Doc(String id) {
                        this.id = id;
                        this.file = new File(folder, id + ".yml");
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
