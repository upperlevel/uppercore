package xyz.upperlevel.uppercore.database.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import xyz.upperlevel.uppercore.database.*;

import java.util.Collections;
import java.util.Map;

import static com.mongodb.MongoCredential.createCredential;

public class MongoDb extends Storage {
    public MongoDb() {
        super("mongodb");
    }

    @Override
    public boolean isSupported() {
        try {
            Class.forName("com.mongodb.MongoClient");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public String[] getDownloadLinks() {
        return new String[]{
                "https://oss.sonatype.org/content/repositories/releases/org/mongodb/mongo-java-driver/3.5.0/mongo-java-driver-3.5.0.jar"
        };
    }

    @Override
    public Database onConnect(String address, int port, String database, String password, String username) {
        // Connection
        MongoCredential credential = createCredential(username, database, password.toCharArray());
        MongoClient client = new MongoClient(new ServerAddress(address, port), Collections.singletonList(credential));

        return new DatabaseImpl(client, client.getDatabase(database));
    }

    // Database
    public class DatabaseImpl implements Database {
        private final MongoClient client;
        private final MongoDatabase db;

        public DatabaseImpl(MongoClient client, MongoDatabase db) {
            this.client = client;
            this.db = db;
        }

        @Override
        public TableImpl table(String id) {
            return new TableImpl(db.getCollection(id));
        }

        // Table
        public class TableImpl implements Table {
            private final MongoCollection<org.bson.Document> table;

            public TableImpl(MongoCollection<org.bson.Document> table) {
                this.table = table;
            }

            @Override
            public DocumentImpl document(String id) {
                return new DocumentImpl(id);
            }

            // Document
            public class DocumentImpl implements Document {
                private final String id;

                public DocumentImpl(String id) {
                    this.id = id;
                }

                @Override
                public Map<String, Object> ask() {
                    return table.find(new org.bson.Document("_id", id)).limit(1).first();
                }

                @Override
                public void send(Map<String, Object> data) {
                    table.replaceOne(
                            new org.bson.Document("_id", id),
                            new org.bson.Document(data).append("_id", id),
                            new UpdateOptions().upsert(true));
                }
            }
        }
    }
}
