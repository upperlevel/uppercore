package xyz.upperlevel.uppercore.database.impl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.database.Database;
import xyz.upperlevel.uppercore.database.Document;
import xyz.upperlevel.uppercore.database.Storage;
import xyz.upperlevel.uppercore.database.Table;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@Deprecated
public class Flatfile extends Storage {
    public Flatfile() {
        super("flatfile");
    }

    @Override
    public boolean isSupported() {
        // We use json.simple that is supported even in 1.8, so it should always be supported
        return true;
    }

    @Override
    public String[] getDownloadLinks() {
        return new String[0];
    }


    @Override
    public Database onConnect(String address, int port, String database, String password, String username) {
        // Database
        File db = new File("plugins", database + File.separator + "db");
        db.mkdirs();

        return new DatabaseImpl(db);
    }

    // Database
    public class DatabaseImpl implements Database {
        private final File db;

        public DatabaseImpl(File db) {
            this.db = db;
        }

        @Override
        public Table table(String id) {
            File folder = new File(db, id);
            folder.mkdirs();

            return new TableImpl(folder);
        }

        // Table
        public class TableImpl implements Table {
            private final File table;

            public TableImpl(File table) {
                this.table = table;
            }

            @Override
            public Document document(String id) {
                File document = new File(table, id + ".json");
                try {
                    document.createNewFile();
                } catch (IOException ignored) {
                }
                return new DocumentImpl(document);
            }

            // Document
            public class DocumentImpl implements Document {
                private final File document;

                public DocumentImpl(File document) {
                    this.document = document;
                }

                @Override
                public Map<String, Object> ask() {
                    JSONParser parser = new JSONParser();
                    try {
                        return (Map<String, Object>) parser.parse(new FileReader(document));
                    } catch (IOException | ParseException e) {
                        return new HashMap<>();
                    }
                }

                @Override
                public void send(Map<String, Object> data) {
                    try (FileWriter writer = new FileWriter(document)) {
                        writer.write(new JSONObject(data).toJSONString());
                        writer.flush();
                    } catch (IOException exception) {
                        throw new IllegalStateException("Cannot write to: " + document.getPath(), exception);
                    }
                }
            }
        }
    }
}
