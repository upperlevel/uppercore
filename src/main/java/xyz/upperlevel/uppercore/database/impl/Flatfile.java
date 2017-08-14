package xyz.upperlevel.uppercore.database.impl;

import com.google.gson.Gson;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.Yaml;
import xyz.upperlevel.uppercore.database.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Flatfile implements Storage {
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
                        this.file = new File(ImplTable.this.getFolder(), id + ".json");
                        try {
                            this.file.createNewFile();
                        } catch (IOException ignored) {
                        }
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public Map<String, Object> ask() {
                        JSONParser parser = new JSONParser();
                        try {
                            return (Map<String, Object>) parser.parse(new FileReader(file));
                        } catch (IOException | ParseException e) {
                            return new HashMap<>();
                        }
                    }

                    @Override
                    public void send(Map<String, Object> data) {
                        try (FileWriter fw = new FileWriter(file)) {
                            fw.write(new JSONObject(data).toJSONString());
                            fw.flush();
                        } catch (IOException e) {
                            throw new IllegalStateException("Error while writing to \"" + file + "\": " + e);
                        }
                    }
                }
            }
        }
    }
}
