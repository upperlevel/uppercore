package xyz.upperlevel.uppercore.database.rethinkdb;

import com.rethinkdb.gen.ast.Get;
import com.rethinkdb.gen.ast.Table;
import lombok.Data;
import xyz.upperlevel.uppercore.database.*;

import java.util.Map;

import static com.rethinkdb.RethinkDB.r;

public class RethinkDb implements Accessor {
    private Credential credential;

    @Override
    public void authenticate(Credential credential) {
        this.credential = credential;
    }

    @Override
    public Connection connect() {
        return connect("localhost", 28015);
    }

    @Override
    public Conn connect(String host, int port) {
        return new Conn(host, port);
    }

    @Data
    public class User {
        private final String user;
        private final char[] pass;
    }

    @Data
    public class Conn implements Connection {
        private final com.rethinkdb.net.Connection conn;

        public Conn(String host, int port) {
            com.rethinkdb.net.Connection.Builder conn = r.connection()
                    .hostname(host)
                    .port(port);
            if (credential != null)
                conn
                        .db(credential.getDatabase())
                        .user(credential.getUsername(), credential.getPassword());
            this.conn = conn.connect();
        }

        @Override
        public Db database(String id) {
            return new Db(id);
        }

        @Override
        public void disconnect() {
            conn.close();
        }

        @Data
        public class Db implements Database {
            private final String id;
            private final com.rethinkdb.gen.ast.Db db;

            public Db(String id) {
                this.id = id;
                this.db = r.db(id);
            }

            @Override
            public Coll collection(String id) {
                db.tableCreate(id)
                        .run(conn);
                return new Coll(id);
            }

            @Data
            public class Coll implements Collection {
                private final String id;
                private final Table table;

                public Coll(String id) {
                    this.id = id;
                    this.table = db.table(id);
                }

                @Override
                public Doc document(String id) {
                    table.insert(r.hashMap("id", id)).run(conn);
                    return new Doc(id);
                }

                @Data
                public class Doc implements Document {
                    private final String id;
                    private final Get doc;

                    public Doc(String id) {
                        this.id = id;
                        this.doc = table.get(id);
                    }

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
