package xyz.upperlevel.uppercore.storage.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MongoDb {
    private MongoDb() {
    }

    public static StorageConnector storage() {
        return new StorageConnectorImpl();
    }

    /* --------------------------------------------------------------------------------- StorageConnectorImpl */
    public static class StorageConnectorImpl extends StorageConnector {
        public StorageConnectorImpl() {
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
                    "https://oss.sonatype.org/content/repositories/releases/org/mongodb/mongo-java-driver/3.8.0/mongo-java-driver-3.8.0.jar"
            };
        }

        @Override
        public Storage connect(Config access) {
            String address = access.getStringRequired("address");
            int port = access.getIntRequired("port");
            MongoClient client;
            if (access.has("database") && access.has("username") && access.has("password")) {
                MongoCredential credential = MongoCredential.createCredential(
                        access.getStringRequired("database"),
                        access.getStringRequired("username"),
                        access.getStringRequired("password").toCharArray()
                );
                client = new MongoClient(new ServerAddress(address, port), Collections.singletonList(credential));
            } else {
                client = new MongoClient(new ServerAddress(address, port));
            }
            return new StorageImpl(client);
        }
    }

    /* --------------------------------------------------------------------------------- Connection */
    public static class StorageImpl implements Storage {
        private final MongoClient m;

        public StorageImpl(MongoClient m) {
            this.m = m;
        }

        @Override
        public Database database(String name) {
            try {
                return new DatabaseImpl(m.getDatabase(name));
            } catch (Exception e) {
                throw new IllegalArgumentException("Database not found: " + name, e);
            }
        }
    }

    /* --------------------------------------------------------------------------------- Database */
    public static class DatabaseImpl implements Database {
        private final MongoDatabase database;

        public DatabaseImpl(MongoDatabase database) {
            this.database = database;
        }

        @Override
        public Table table(String name) {
            return new TableImpl(database.getCollection(name));
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final MongoCollection<org.bson.Document> collection;

        public TableImpl(MongoCollection<org.bson.Document> collection) {
            this.collection = collection;
        }

        @Override
        public Element element(String id) {
            try {
                collection.insertOne(new Document("_id", id));
            } catch (Exception ignored) {
            }
            return new ElementImpl(this, id);
        }
    }

    /* --------------------------------------------------------------------------------- Document */
    public static class ElementImpl implements Element {
        private final MongoCollection<Document> collection;
        private final String id;

        public ElementImpl(TableImpl table, String id) {
            this.collection = table.collection;
            this.id = id;
        }

        @Override
        public Object get(String parameter) {
            Document projection = new Document();
            String[] path = parameter.split("\\.");
            int i;
            Document tmp = null;
            for (i = 0; i < path.length - 1; i++) {
                tmp = new Document();
                projection.put(path[i], tmp);
            }
            if (tmp != null) {
                tmp.put(path[i], 1);
            }
            return collection.find(new Document("_id", id)).projection(projection).first();
        }

        @Override
        public Map<String, Object> getAll() {
            return collection.find(new Document("_id", id)).first();
        }

        @Override
        public void update(Map<String, Object> data) {
            Document toReplace = new Document(data);
            toReplace.put("_id", id);
            collection.replaceOne(new Document("_id", id), toReplace, new UpdateOptions().upsert(true));
        }

        @Override
        public void drop() {
            collection.deleteOne(new Document("_id", id));
        }
    }
}
