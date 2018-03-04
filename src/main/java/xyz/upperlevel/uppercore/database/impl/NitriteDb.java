package xyz.upperlevel.uppercore.database.impl;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import xyz.upperlevel.uppercore.database.*;

import java.util.Map;

import static org.dizitart.no2.UpdateOptions.updateOptions;
import static org.dizitart.no2.filters.Filters.eq;

public class NitriteDb extends Storage {
    public NitriteDb() {
        super("local_db");
    }

    @Override
    public boolean isSupported() {
        try {
            Class.forName("org.dizitart.Nitrite");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public String[] getDownloadLinks() {
        return new String[]{"https://oss.sonatype.org/content/repositories/releases/org/dizitart/nitrite/2.1.1/nitrite-2.1.1.jar"};
    }

    @Override
    public Database onConnect(String address, int port, String database, String password, String username) {
        return new DatabaseImpl(Nitrite.builder()
                .compressed()
                .filePath("plugins/storage/" + database + ".db")
                .openOrCreate(username, password));
    }

    // Database
    public class DatabaseImpl implements Database {
        private Nitrite db;

        public DatabaseImpl(Nitrite db) {
            this.db = db;
        }

        @Override
        public TableImpl table(String id) {
            return new TableImpl(db.getCollection(id));
        }


        // Table
        public class TableImpl implements Table {
            private NitriteCollection table;

            public TableImpl(NitriteCollection table) {
                this.table = table;
            }

            @Override
            public DocumentImpl document(String id) {
                return new DocumentImpl(id);
            }

            // Document
            public class DocumentImpl implements Document {
                private String id;

                public DocumentImpl(String id) {
                    this.id = id;
                }

                @Override
                public Map<String, Object> ask() {
                    return table.find(eq("_id", id)).firstOrDefault();
                }

                @Override
                public void send(Map<String, Object> data) {
                    table.update(eq("_id", id), new org.dizitart.no2.Document(data).put("_id", id), updateOptions(true));
                }
            }
        }
    }
}
