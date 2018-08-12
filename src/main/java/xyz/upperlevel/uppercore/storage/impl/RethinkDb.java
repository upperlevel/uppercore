package xyz.upperlevel.uppercore.storage.impl;

import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Get;
import com.rethinkdb.gen.ast.ReqlExpr;
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
            return new ConnectionImpl(builder.connect());
        }
    }

    /* --------------------------------------------------------------------------------- Connection */
    public static class ConnectionImpl implements Storage {
        private final com.rethinkdb.net.Connection connection;

        public ConnectionImpl(com.rethinkdb.net.Connection connection) {
            this.connection = connection;
        }

        @Override
        public Database database(String name) {
            try {
                r.dbCreate(name).run(connection);
            } catch (Exception ignored) {
            }
            return new DatabaseImpl(connection, r.db(name));
        }
    }

    /* --------------------------------------------------------------------------------- Database */
    public static class DatabaseImpl implements Database {
        private final com.rethinkdb.net.Connection connection;
        private final Db db;

        public DatabaseImpl(com.rethinkdb.net.Connection connection, Db db) {
            this.connection = connection;
            this.db = db;
        }

        @Override
        public Table table(String id) {
            try {
                db.tableCreate(id).run(connection);
            } catch (Exception ignored) {
            }
            return new TableImpl(connection, db.table(id));
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final com.rethinkdb.net.Connection connection;
        private final com.rethinkdb.gen.ast.Table table;

        public TableImpl(com.rethinkdb.net.Connection connection, com.rethinkdb.gen.ast.Table table) {
            this.connection = connection;
            this.table = table;
        }

        @Override
        public Element element(String id) {
            try {
                table.insert(r.hashMap("id", id)).run(connection);
            } catch (Exception ignored) {
            }
            return new ElementImpl(connection, id, table.get(id));
        }
    }

    /* --------------------------------------------------------------------------------- Element */
    public static class ElementImpl implements Element {
        private final com.rethinkdb.net.Connection connection;
        private final String id;
        private final Get document;

        public ElementImpl(com.rethinkdb.net.Connection connection, String id, Get document) {
            this.connection = connection;
            this.id = id;
            this.document = document;
        }

        @Override
        public Object get(String parameter) {
            ReqlExpr field = document;
            for (String step : parameter.split("\\.")) {
                field = field.getField(step);
            }
            return field.run(connection);
        }

        @Override
        public Map<String, Object> getAll() {
            return document.run(connection);
        }

        @Override
        public void update(Map<String, Object> data) {
            data.put("id", id);
            document.replace(data).run(connection);
        }

        @Override
        public void drop() {
            document.delete().run(connection);
        }

    }
}
