package xyz.upperlevel.uppercore.database.impl;

import com.rethinkdb.gen.ast.Get;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.uppercore.database.*;

import java.util.Map;

import static com.rethinkdb.RethinkDB.r;

public class RethinkDb implements Storage {
    @Override
    public String getId() {
        return "rethinkdb";
    }

    @Override
    public Connection connect(String database) {
        return connect(database, "localhost", 28015);
    }

    @Override
    public Connection connect(String database, String host, int port) {
        return new ConnectionImpl(database, r.connection()
                .hostname(host)
                .port(port)
                .db(database)
                .connect());
    }

    @Override
    public Connection connect(String database, String host, int port, String user, String password) {
        return new ConnectionImpl(database, r.connection()
                .hostname(host)
                .port(port)
                .db(database)
                .user(user, password)
                .connect());
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
        return new String[] {
                "https://oss.sonatype.org/content/repositories/releases/com/rethinkdb/rethinkdb-driver/2.3.3/rethinkdb-driver-2.3.3.jar"
        };
    }

    @RequiredArgsConstructor
    public class ConnectionImpl implements Connection {
        @Getter
        private final String db;
        private final com.rethinkdb.net.Connection conn;

        @Override
        public DatabaseImpl database() {
            try {
                r.dbCreate(db).run(conn);
            } catch (Exception ignored) {
            }
            return new DatabaseImpl(r.db(db));
        }

        @RequiredArgsConstructor
        public class DatabaseImpl implements Database {
            private final com.rethinkdb.gen.ast.Db db;

            @Override
            public TableImpl table(String id) {
                try {
                    r.tableCreate(id).run(conn);
                } catch (Exception ignored) {
                }
                return new TableImpl(db.table(id));
            }

            @RequiredArgsConstructor
            public class TableImpl implements Table {
                private final com.rethinkdb.gen.ast.Table table;

                @Override
                public DocumentImpl document(String id) {
                    try {
                        table.insert(r.hashMap("id", id)).run(conn);
                    } catch (Exception ignored) {
                    }
                    return new DocumentImpl(id, table.get(id));
                }

               @RequiredArgsConstructor
                public class DocumentImpl implements Document {
                    @Getter
                    private final String id;
                    private final Get doc;

                    @Override
                    public Map<String, Object> ask() {
                        return doc.run(conn);
                    }

                    @Override
                    public void send(Map<String, Object> data) {
                        data.put("id", id);
                        doc.replace(data).run(conn);
                    }
                }
            }
        }
    }
}
