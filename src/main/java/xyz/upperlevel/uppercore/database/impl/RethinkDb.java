package xyz.upperlevel.uppercore.database.impl;

import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Get;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Connection.Builder;
import xyz.upperlevel.uppercore.database.Database;
import xyz.upperlevel.uppercore.database.Document;
import xyz.upperlevel.uppercore.database.Storage;
import xyz.upperlevel.uppercore.database.Table;

import java.util.Map;

import static com.rethinkdb.RethinkDB.r;

public class RethinkDb extends Storage {
    public RethinkDb() {
        super("rethinkdb");
    }

    @Override
    public boolean isSupported() {
        try {
            Class.forName("com.rethinkdb.RethinkDB");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public String[] getDownloadLinks() {
        return new String[]{
                "https://oss.sonatype.org/content/repositories/releases/com/rethinkdb/rethinkdb-driver/2.3.3/rethinkdb-driver-2.3.3.jar"
        };
    }

    @Override
    public Database onConnect(String address, int port, String database, String password, String username) {
        // Connection
        Builder builder = r.connection()
                .hostname(address)
                .port(port);
        if (username != null && password != null) {
            builder.user(username, password);
        }
        Connection connection = builder.connect();
        // Database
        try {
            r.dbCreate(database).run(connection);
        } catch (Exception ignored) {
        }
        return new DatabaseImpl(connection, r.db(database));
    }

    // Database
    public class DatabaseImpl implements Database {
        private final Connection connection;
        private final Db db;

        public DatabaseImpl(Connection connection, Db db) {
            this.connection = connection;
            this.db = db;
        }

        @Override
        public TableImpl table(String id) {
            try {
                r.tableCreate(id).run(connection);
            } catch (Exception ignored) {
            }
            return new TableImpl(db.table(id));
        }

        // Table
        public class TableImpl implements Table {
            private final com.rethinkdb.gen.ast.Table table;

            public TableImpl(com.rethinkdb.gen.ast.Table table) {
                this.table = table;
            }

            @Override
            public DocumentImpl document(String id) {
                try {
                    table.insert(r.hashMap("id", id)).run(connection);
                } catch (Exception ignored) {
                }
                return new DocumentImpl(id, table.get(id));
            }

            // Document
            public class DocumentImpl implements Document {
                private final String id;
                private final Get document;

                public DocumentImpl(String id, Get document) {
                    this.id = id;
                    this.document = document;
                }

                @Override
                public Map<String, Object> ask() {
                    return document.run(connection);
                }

                @Override
                public void send(Map<String, Object> data) {
                    data.put("id", id);
                    document.replace(data).run(connection);
                }
            }
        }
    }
}
