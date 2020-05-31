package xyz.upperlevel.uppercore.storage.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static xyz.upperlevel.uppercore.storage.DuplicatePolicy.KEEP_OLD;
import static xyz.upperlevel.uppercore.storage.DuplicatePolicy.REPLACE;

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
            return new String[] {
                    "https://oss.sonatype.org/content/repositories/releases/org/mongodb/mongo-java-driver/3.12.4/mongo-java-driver-3.12.4.jar"
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
        private final MongoDatabase db;

        public DatabaseImpl(MongoDatabase db) {
            this.db = db;
        }

        @Override
        public boolean create() {
            return false; // Always open
        }

        @Override
        public boolean drop() {
            db.drop();
            return true;
        }

        @Override
        public Table table(String name) {
            return new TableImpl(db.getCollection(name));
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final MongoCollection<org.bson.Document> coll;

        public TableImpl(MongoCollection<Document> coll) {
            this.coll = coll;
        }

        @Override
        public boolean create() {
            // On MongoDB db.getCollection() does the job
            return false;
        }

        @Override
        public boolean drop() {
            coll.drop();
            return true;
        }

        @Override
        public Element element(String id) {
            return new ElementImpl(this, id);
        }
    }

    /* --------------------------------------------------------------------------------- Document */
    public static class ElementImpl implements Element {
        private final MongoCollection<Document> coll;
        private final String id;

        public ElementImpl(TableImpl table, String id) {
            this.coll = table.coll;
            this.id = id;
        }

        @Override
        public boolean insert(Map<String, Object> data, DuplicatePolicy duplicatePolicy) {
            Document newData = new Document(data);
            newData.put("_id", id);
            if (duplicatePolicy == REPLACE) {
                UpdateResult res = coll.replaceOne(new Document("_id", id), newData, new ReplaceOptions().upsert(true));
                return res.getModifiedCount() > 0;
            } else if (duplicatePolicy == KEEP_OLD){
                try {
                    coll.insertOne(newData);
                    return true;
                } catch (Exception e) {
                    // Thrown if element already inserted
                    return false;
                }
            } else if (duplicatePolicy == DuplicatePolicy.MERGE) {
                UpdateResult res = coll.updateOne(new Document("_id", id), new Document(data));
                return res.getModifiedCount() > 0;
            } else {
                throw new IllegalStateException();
            }
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
            return coll.find(new Document("_id", id)).projection(projection).first();
        }

        @Override
        public Optional<Map<String, Object>> getData() {
            return Optional.ofNullable(coll.find(new Document("_id", id)).first());
        }

        @Override
        public boolean drop() {
            DeleteResult res = coll.deleteOne(new Document("_id", id));
            return res.getDeletedCount() > 0;
        }
    }
}
