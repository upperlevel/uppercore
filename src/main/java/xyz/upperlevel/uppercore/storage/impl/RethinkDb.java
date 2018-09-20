package xyz.upperlevel.uppercore.storage.impl;

import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Get;
import com.rethinkdb.gen.ast.ReqlExpr;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.util.Map;

import static com.rethinkdb.RethinkDB.r;
import static com.rethinkdb.net.Connection.*;

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
            return new String[]{
                    "https://oss.sonatype.org/content/repositories/releases/com/rethinkdb/rethinkdb-driver/2.3.3/rethinkdb-driver-2.3.3.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/slf4j/slf4j-api/1.7.12/slf4j-api-1.7.12.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar"
            };
        }

        @Override
        public Storage connect(Config access) {
            Builder builder = r.connection()
                    .hostname(access.getStringRequired("address"))
                    .port(access.getIntRequired("port"));
            if (access.has("username")) {
                builder.user(access.getString("username"), access.getString("password", ""));
            }
            return new StorageImpl(builder.connect());
        }
    }

    /* --------------------------------------------------------------------------------- Connection */
    public static class StorageImpl implements Storage {
        private final Connection conn;

        public StorageImpl(com.rethinkdb.net.Connection conn) {
            this.conn = conn;
        }

        @Override
        public Database database(String name) {
            return new DatabaseImpl(conn, name);
        }
    }

    /* --------------------------------------------------------------------------------- Database */
    public static class DatabaseImpl implements Database {
        private final Connection conn;
        private final String name;

        public DatabaseImpl(Connection conn, String name) {
            this.conn = conn;
            this.name = name;
        }

        @Override
        public boolean create() {
            try {
                MapObject res = r.dbCreate(name).run(conn);
                return ((int) res.get("dbs_created")) > 0;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean drop() {
            try {
                MapObject res = r.dbDrop(name).run(conn);
                return ((Number) res.get("dbs_dropped")).intValue() > 0;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public Table table(String name) {
            return new TableImpl(conn, r.db(this.name), name);
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
        public boolean insert(Map<String, Object> data, boolean replace) {
            if (replace) {
                MapObject res = table.insert(data).run(conn);
                return ((Number) res.get("inserted")).intValue() > 0;
            } else {
                data.remove("id");
                MapObject res = doc.replace(data).run(conn);
                return ((Number) res.get("replaced")).intValue() > 0;
            }
        }

        @Override
        public boolean update(Map<String, Object> data) {
            MapObject res = doc.update(data).run(conn);
            return ((Number) res.get("replaced")).intValue() > 0;
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
        public Map<String, Object> getData() {
            return doc.run(conn);
        }

        @Override
        public boolean drop() {
            MapObject res = doc.delete().run(conn);
            return ((Number) res.get("deleted")).intValue() > 0;
        }

    }
}