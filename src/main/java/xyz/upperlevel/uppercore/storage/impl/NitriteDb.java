package xyz.upperlevel.uppercore.storage.impl;

import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.UpdateOptions;
import org.dizitart.no2.filters.Filters;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class NitriteDb {
    private NitriteDb() {
    }

    public static StorageConnector storage() {
        return new StorageConnectorImpl();
    }

    /* --------------------------------------------------------------------------------- Storage */
    public static class StorageConnectorImpl extends StorageConnector {
        public StorageConnectorImpl() {
            super("nitritedb");
        }

        @Override
        public boolean isSupported() {
            try {
                Class.forName("org.dizitart.no2.Nitrite");
            } catch (ClassNotFoundException e) {
                return false;
            }
            return true;
        }

        @Override
        public String[] getDownloadLinks() {
            return new String[]{
                    "https://oss.sonatype.org/content/repositories/releases/org/dizitart/nitrite/3.0.2/nitrite-3.0.2.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/h2database/h2-mvstore/1.4.196/h2-mvstore-1.4.196.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/objenesis/objenesis/2.6/objenesis-2.6.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/fasterxml/jackson/core/jackson-databind/2.9.5/jackson-databind-2.9.5.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/fasterxml/jackson/core/jackson-annotations/2.9.5/jackson-annotations-2.9.5.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/jasypt/jasypt/1.9.2/jasypt-1.9.2.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/squareup/okhttp3/okhttp/3.9.1/okhttp-3.9.1.jar",
                    "https://oss.sonatype.org/content/repositories/releases/uk/co/jemos/podam/podam/7.1.0.RELEASE/podam-7.1.0.RELEASE.jar"
            };
        }

        @Override
        public Storage connect(Config access) {
            return new StorageImpl();
        }
    }

    /* --------------------------------------------------------------------------------- Connection */
    public static class StorageImpl implements Storage {
        public StorageImpl() {
        }

        @Override
        public Database database(String name) {
            File folder = new File("storage");
            folder.mkdir();

            return new DatabaseImpl(Nitrite.builder()
                    .compressed()
                    .filePath("storage/" + name + ".db")
                    .openOrCreate());
        }
    }

    /* --------------------------------------------------------------------------------- Database */
    public static class DatabaseImpl implements Database {
        private final Nitrite db;

        public DatabaseImpl(Nitrite db) {
            this.db = db;
        }

        @Override
        public Table table(String name) {
            return new TableImpl(db.getCollection(name));
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final NitriteCollection collection;

        public TableImpl(NitriteCollection collection) {
            this.collection = collection;
        }

        @Override
        public Element element(String id) {
            return new ElementImpl(collection, id);
        }
    }

    /* --------------------------------------------------------------------------------- Element */

    // On NitriteDb, "_id" field can't be replaced by
    // a custom value (like in MongoDb) so, to identify elements, we use just "id".

    public static class ElementImpl implements Element {
        private final NitriteCollection collection;
        private final String id;

        public ElementImpl(NitriteCollection collection, String id) {
            this.collection = collection;
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
            return collection.find(Filters.eq("id", id)).project(projection).firstOrDefault();
        }

        @Override
        public Map<String, Object> getAll() {
            return collection.find(Filters.eq("id", id)).firstOrDefault();
        }

        @Override
        public void update(Map<String, Object> data) {
            collection.update(Filters.eq("id", id), new Document(data).put("id", id), UpdateOptions.updateOptions(true));
        }

        @Override
        public void drop() {
            collection.remove(Filters.eq("id", id));
        }
    }
}
