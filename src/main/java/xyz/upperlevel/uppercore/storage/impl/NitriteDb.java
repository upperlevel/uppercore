package xyz.upperlevel.uppercore.storage.impl;

import org.dizitart.no2.*;
import org.dizitart.no2.filters.Filters;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import static xyz.upperlevel.uppercore.storage.DuplicatePolicy.*;

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
            return new String[] {
                    "https://oss.sonatype.org/content/repositories/releases/org/dizitart/nitrite/3.4.1/nitrite-3.4.1.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/h2database/h2-mvstore/1.4.200/h2-mvstore-1.4.200.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/objenesis/objenesis/2.6/objenesis-2.6.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/fasterxml/jackson/core/jackson-databind/2.10.1/jackson-databind-2.10.1.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/fasterxml/jackson/core/jackson-annotations/2.10.1/jackson-annotations-2.10.1.jar",
                    "https://oss.sonatype.org/content/repositories/releases/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar",
                    "https://oss.sonatype.org/content/repositories/releases/com/squareup/okhttp3/okhttp/4.3.1/okhttp-4.3.1.jar",
                    "https://oss.sonatype.org/content/repositories/releases/uk/co/jemos/podam/podam/7.2.3.RELEASE/podam-7.2.3.RELEASE.jar"
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
            return new DatabaseImpl(name);
        }
    }

    /* --------------------------------------------------------------------------------- Database */
    public static class DatabaseImpl implements Database {
        private final String name;
        private final File file;

        private Nitrite db;

        public DatabaseImpl(String name) {
            this.name = name;
            this.file = new File(Uppercore.plugin().getDataFolder(), "storage/nitrite/" + name + ".db");
        }

        private Nitrite ensureOpened() {
            if (db == null) {
                this.file.getParentFile().mkdirs();
                db = Nitrite.builder().compressed().filePath(this.file).openOrCreate();
            }
            return db;
        }

        @Override
        public boolean create() {
            boolean notPresent = db == null && !this.file.exists();
            ensureOpened();
            return notPresent; // Before
        }

        @Override
        public boolean drop() {
            if (db != null) {
                db.close();
                db = null;
            }
            return file.delete();
        }

        @Override
        public Table table(String name) {
            ensureOpened();
            return new TableImpl(db.getCollection(name));
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final NitriteCollection coll;

        public TableImpl(NitriteCollection coll) {
            this.coll = coll;
        }

        @Override
        public boolean create() {
            return false;
        }

        @Override
        public boolean drop() {
            coll.drop();
            return true;
        }

        @Override
        public Element element(String id) {
            return new ElementImpl(coll, id);
        }
    }

    /* --------------------------------------------------------------------------------- Element */

    // On NitriteDb, "_id" field can't be replaced by
    // a custom value (like in MongoDb) so, to identify elements, we use just "id".

    public static class ElementImpl implements Element {
        private final NitriteCollection coll;
        private final String id;

        public ElementImpl(NitriteCollection coll, String id) {
            this.coll = coll;
            this.id = id;
        }

        @Override
        public boolean insert(Map<String, Object> data, DuplicatePolicy policy) {
            Document newData = new Document(data);
            newData.put("id", id);
            if (policy == REPLACE) {
                WriteResult res = coll.update(Filters.eq("id", id), newData, UpdateOptions.updateOptions(true));
                return res.getAffectedCount() > 0;
            } else if (policy == KEEP_OLD) {
                WriteResult res = coll.insert(newData);
                return res.getAffectedCount() > 0;
            } else if (policy == MERGE) {
                WriteResult res = coll.update(Filters.eq("id", id), new Document(data));
                return res.getAffectedCount() > 0;
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
            return coll.find(Filters.eq("id", id)).project(projection).firstOrDefault();
        }

        @Override
        public Optional<Map<String, Object>> getData() {
            return Optional.ofNullable(coll.find(Filters.eq("id", id)).firstOrDefault());
        }

        @Override
        public boolean drop() {
            WriteResult res = coll.remove(Filters.eq("id", id));
            return res.getAffectedCount() > 0;
        }
    }
}
