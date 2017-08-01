package xyz.upperlevel.uppercore.database.impl;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import xyz.upperlevel.uppercore.database.*;

import java.util.Map;

import static com.mongodb.MongoCredential.createCredential;
import static java.util.Collections.singletonList;

public class MongoDb implements DatabaseDriver {
    @Override
    public String getId() {
        return "mongodb";
    }

    @Override
    public Connection connect(String database) {
        return connect(database, "localhost", 27017);
    }

    @Override
    public Connection connect(String database, String host, int port) {
        return new ImplConnection(database, new MongoClient(host, port));
    }

    @Override
    public Connection connect(String database, String host, int port, String user, String password) {
        return new ImplConnection(database, new MongoClient(
                new ServerAddress(host, port),
                singletonList(createCredential(user, database, password.toCharArray()))
        ));
    }

    @Data
    public class ImplConnection implements Connection {
        private final String db;
        private final MongoClient client;

        public ImplConnection(String db, MongoClient client) {
            this.db = db;
            this.client = client;
        }

        @Override
        public ImplDatabase database() {
            return new ImplDatabase();
        }

        @Data
        public class ImplDatabase implements Database {
            private final MongoDatabase db;

            public ImplDatabase() {
                this.db = client.getDatabase(ImplConnection.this.db);
            }

            @Override
            public ImplTable table(String id) {
                db.createCollection(id);
                return new ImplTable(id);
            }

            @Data
            public class ImplTable implements Table {
                private final String id;
                private final MongoCollection<org.bson.Document> table;

                public ImplTable(String id) {
                    this.id = id;
                    this.table = db.getCollection(id);
                }

                @Override
                public ImplDocument document(String id) {
                    table.insertOne(new org.bson.Document("_id", id));
                    return new ImplDocument(id);
                }

                @Data
                public class ImplDocument implements Document {
                    private final String id;
                    private final org.bson.Document doc;

                    public ImplDocument(String id) {
                        this.id = id;
                        this.doc = table.find(new org.bson.Document("_id", id)).limit(1).first();
                    }

                    @Override
                    public Map<String, Object> ask() {
                        return doc;
                    }

                    @Override
                    public void send(Map<String, Object> data) {
                        table.updateOne(new org.bson.Document("_id", id), new org.bson.Document(data).append("_id", id));
                    }
                }
            }
        }
    }
}
