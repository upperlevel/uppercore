package xyz.upperlevel.uppercore.storage.impl;

import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Get;
import com.rethinkdb.gen.ast.ReqlExpr;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.util.Map;
import java.util.Optional;

import static com.rethinkdb.RethinkDB.r;
import static com.rethinkdb.net.Connection.Builder;
import static xyz.upperlevel.uppercore.storage.DuplicatePolicy.*;

public final class RethinkDb {
    private RethinkDb() {
    }

    public static StorageConnector storage() {
        return new StorageConnectorImpl();
    }

    /* --------------------------------------------------------------------------------- Storage */
    public static class StorageConnectorImpl extends StorageConnector {
        public StorageConnectorImpl() {
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
            return new String[] {
                    "https://oss.sonatype.org/content/repositories/releases/com/rethinkdb/rethinkdb-driver/2.3.3/rethinkdb-driver-2.3.3.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/slf4j/slf4j-api/1.7.12/slf4j-api-1.7.12.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar"
            };
        }

        @Override
        public Storage connect(Config access) {
            Builder builder = r.connection()
                    .hostname(access.getStringRequired("address"))
                    .port(access.getInt("port", 28015));
            if (access.has("username")) {
                builder.user(access.getString("username"), access.getString("password", ""));
            }
            return new StorageImpl(builder.connect(), access.getStringRequired("database"));
        }
    }

    /* --------------------------------------------------------------------------------- Connection */
    public static class StorageImpl implements Storage {
        private final Connection conn;
        private Db db;

        public StorageImpl(com.rethinkdb.net.Connection conn, String dbName) {
            this.conn = conn;
            this.db = r.db(dbName);
        }

        @Override
        public Table table(String name) {
            return new TableImpl(conn, db, name);
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final Connection conn;
        private final Db db;
        private final String name;

        public TableImpl(Connection conn, Db db, String name) {
            this.conn = conn;
            this.db = db;
            this.name = name;
        }

        @Override
        public boolean create() {
            try {
                MapObject res = db.tableCreate(name).run(conn);
                return ((Number) res.get("tables_created")).intValue() > 0;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean drop() {
            try {
                MapObject res = db.tableDrop(name).run(conn);
                return ((Number) res.get("tables_dropped")).intValue() > 0;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public Element element(String id) {
            com.rethinkdb.gen.ast.Table table = db.table(name);
            return new ElementImpl(conn, table, table.get(id));
        }
    }

    /* --------------------------------------------------------------------------------- Element */
    public static class ElementImpl implements Element {
        private final Connection conn;
        private final com.rethinkdb.gen.ast.Table table;
        private final Get doc;

        public ElementImpl(Connection conn, com.rethinkdb.gen.ast.Table table, Get doc) {
            this.conn = conn;
            this.table = table;
            this.doc = doc;
        }

        @Override
        public boolean insert(Map<String, Object> data, DuplicatePolicy duplicatePolicy) {
            if (duplicatePolicy == REPLACE) {
                data.remove("id");
                MapObject res = doc.replace(data).run(conn);
                return ((Number) res.get("replaced")).intValue() > 0;
            } else if (duplicatePolicy == KEEP_OLD) {
                MapObject res = table.insert(data).run(conn);
                return ((Number) res.get("inserted")).intValue() > 0;
            } else if (duplicatePolicy == MERGE) {
                MapObject res = doc.update(data).run(conn);
                return ((Number) res.get("replaced")).intValue() > 0;
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public Object get(String parameter) {
            ReqlExpr field = doc;
            for (String step : parameter.split("\\.")) {
                field = field.getField(step);
            }
            return field.run(conn);
        }

        @Override
        public Optional<Map<String, Object>> getData() {
            // TODO: test
            return Optional.ofNullable(doc.run(conn));
        }

        @Override
        public boolean drop() {
            MapObject res = doc.delete().run(conn);
            return ((Number) res.get("deleted")).intValue() > 0;
        }
    }
}
