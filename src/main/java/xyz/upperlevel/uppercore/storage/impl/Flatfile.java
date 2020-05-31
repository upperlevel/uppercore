package xyz.upperlevel.uppercore.storage.impl;

import com.google.gson.JsonParseException;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static xyz.upperlevel.uppercore.util.JsonUtil.GENERAL_GSON;
import static xyz.upperlevel.uppercore.util.JsonUtil.JSON_MAP_TYPE;

@SuppressWarnings("unchecked")
@Deprecated
public final class Flatfile {
    private Flatfile() {
    }

    public static StorageConnector storage() {
        return new StorageConnectorImpl();
    }

    /* --------------------------------------------------------------------------------- Storage */
    public static class StorageConnectorImpl extends StorageConnector {
        public StorageConnectorImpl() {
            super("flatfile");
        }

        @Override
        public boolean isSupported() {
            // We use json.simple that is supported even in 1.8, so it should always be supported
            return true;
        }

        @Override
        public String[] getDownloadLinks() {
            return new String[0];
        }

        @Override
        public Storage connect(Config access) {
            Logger l = Uppercore.logger();
            l.warning("Flatfile storage is deprecated and it might be removed on future versions");
            l.warning("Please use 'nitritedb' as it's a better storage");
            // No auth
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
        private final File folder;

        public DatabaseImpl(String name) {
            this.name = name;
            this.folder =  new File(Uppercore.plugin().getDataFolder(), "storage/flatfile/" + name + ".db");
        }

        @Override
        public boolean create() {
            if (!folder.exists()) {
                folder.mkdirs();
                return true;
            }
            return false;
        }

        @Override
        public boolean drop() {
            return folder.delete();
        }

        @Override
        public Table table(String name) {
            return new TableImpl(folder, name);
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final String name;
        private final File folder;

        public TableImpl(File dbFolder, String name) {
            this.name = name;
            this.folder =  new File(dbFolder, name);
        }

        @Override
        public boolean create() {
            return folder.mkdirs();
        }

        @Override
        public boolean drop() {
            return folder.delete();
        }

        @Override
        public Element element(String id) {
            File file = new File(folder, id + ".json");
            return new ElementImpl(id, file);
        }
    }

    /* --------------------------------------------------------------------------------- Element */
    public static class ElementImpl implements Element {
        private final String id;
        private final File file;

        public ElementImpl(String id, File file) {
            this.id = id;
            this.file = file;
        }

        @Override
        public boolean insert(Map<String, Object> data, DuplicatePolicy duplicatePolicy) {
            if (duplicatePolicy == DuplicatePolicy.KEEP_OLD && file.exists()) {
                return false;
            }

            Map<String, Object> writeData = null;

            if (duplicatePolicy == DuplicatePolicy.MERGE) {
                writeData = getData().orElseGet(HashMap::new);
                writeData.putAll(data);
            } else {
                writeData = data;
            }

            if (duplicatePolicy == DuplicatePolicy.REPLACE) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(GENERAL_GSON.toJson(writeData));
                    writer.flush();
                } catch (IOException e) {
                    throw new IllegalStateException("Can't write to file: " + file, e);
                }
            }
            return true;
        }

        @Override
        public Object get(String parameter) {
            String prev = null;
            Object tree = getData().orElse(null);

            if (tree == null) return null;

            for (String step : parameter.split("\\.")) {
                if (tree instanceof Map) {
                    tree = ((Map<String, Object>) tree).get(step);
                } else {
                    throw new IllegalArgumentException("There is no '" + step + "' on parameter '" + prev + "' for element: " + id);
                }
                prev = step;
            }
            return tree;
        }

        @Override
        public Optional<Map<String, Object>> getData() {
            if (!file.exists()) return Optional.empty();

            try {
                return Optional.of(GENERAL_GSON.fromJson(new FileReader(file), JSON_MAP_TYPE));
            } catch (JsonParseException | IOException e) {
                throw new IllegalStateException("Can't read element file: " + file.getPath(), e);
            }
        }

        @Override
        public boolean drop() {
            return file.delete();
        }
    }
}
