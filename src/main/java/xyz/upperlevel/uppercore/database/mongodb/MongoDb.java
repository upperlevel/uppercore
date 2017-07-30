package xyz.upperlevel.uppercore.database.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import xyz.upperlevel.uppercore.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MongoDb implements Accessor {
    private final List<MongoCredential> credentials = new ArrayList<>();

    @Override
    public void authenticate(Credential credential) {
        credentials.add(MongoCredential.createCredential(
                credential.getUsername(),
                credential.getDatabase(),
                credential.getPassword().toCharArray()
        ));
    }

    @Override
    public Connection connect() {
        return connect("localhost", 27017);
    }

    @Override
    public Conn connect(String host, int port) {
        return new Conn(host, port);
    }

    @Data
    public class Conn implements Connection {
        private final MongoClient client;

        public Conn(String host, int port) {
            client = new MongoClient(new ServerAddress(host, port), credentials);
        }

        @Override
        public Db database(String id) {
            return new Db(id);
        }

        @Override
        public void disconnect() {
        }

        @Data
        public class Db implements Database {
            private final String id;
            private final MongoDatabase db;

            public Db(String id) {
                this.id = id;
                this.db = client.getDatabase(id);
            }

            @Override
            public Coll collection(String id) {
                db.createCollection(id);
                return new Coll(id);
            }

            @Data
            public class Coll implements Collection {
                private final String id;
                private final MongoCollection<org.bson.Document> coll;

                public Coll(String id) {
                    this.id = id;
                    this.coll = db.getCollection(id);
                }

                @Override
                public Doc document(String id) {
                    coll.insertOne(new org.bson.Document("_id", id));
                    return new Doc(id);
                }

                @Data
                public class Doc implements Document {
                    private final String id;
                    private final org.bson.Document doc;

                    public Doc(String id) {
                        this.id = id;
                        this.doc = coll.find(new org.bson.Document("_id", id)).limit(1).first();
                    }

                    @Override
                    public Map<String, Object> ask() {
                        return doc;
                    }

                    @Override
                    public void send(Map<String, Object> data) {
                        coll.updateOne(new org.bson.Document("_id", id), new org.bson.Document(data).append("_id", id));
                    }
                }
            }
        }
    }
}
