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
import java.util.regex.Pattern;

import static xyz.upperlevel.uppercore.util.JsonUtil.GENERAL_GSON;
import static xyz.upperlevel.uppercore.util.JsonUtil.JSON_MAP_TYPE;

@SuppressWarnings("unchecked")
@Deprecated
public final class Flatfile {
    public static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[-a-zA-Z0-9_]+$");

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
            l.warning("Please use 'nitritedb' as it's a better storage method");
            // No auth
            return new StorageImpl(access.getString("database", "default"));
        }
    }

    /* --------------------------------------------------------------------------------- Connection */
    public static class StorageImpl implements Storage {
        private final File folder;

        public StorageImpl(String db) {
            this.folder =  new File(Uppercore.plugin().getDataFolder(), "storage/flatfile/" + db + ".db");
        }

        @Override
        public Table table(String name) {
            if (!TABLE_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException("Invalid table name");
            }
            return new TableImpl(this.folder, name);
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final String name;
        private final File folder;

        public TableImpl(File folder, String name) {
            this.name = name;
            this.folder = new File(folder, name);
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
            if (!file.getAbsoluteFile().getParentFile().equals(folder.getAbsoluteFile())) {
                throw new IllegalArgumentException("Invalid element id");
            }
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
