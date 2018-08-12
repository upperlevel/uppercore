package xyz.upperlevel.uppercore.storage.impl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

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
            File folder = new File("plugins", name + File.separator + "db");
            folder.mkdirs();
            return new DatabaseImpl(folder);
        }
    }

    /* --------------------------------------------------------------------------------- Database */
    public static class DatabaseImpl implements Database {
        private final File folder;

        public DatabaseImpl(File folder) {
            this.folder = folder;
        }

        @Override
        public Table table(String name) {
            File sub = new File(folder, name);
            sub.mkdirs();
            return new TableImpl(sub);
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final File folder;

        public TableImpl(File folder) {
            this.folder = folder;
        }

        @Override
        public Element element(String id) {
            File file = new File(folder, id + ".json");
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Can't create element file: " + file.getName(), e);
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
        public Object get(String parameter) {
            String prev = null;
            Object tree = getAll();
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
        public Map<String, Object> getAll() {
            JSONParser parser = new JSONParser();
            try {
                return (Map<String, Object>) parser.parse(new FileReader(file));
            } catch (IOException | ParseException e) {
                throw new IllegalStateException("Can't read element file: " + file.getPath(), e);
            }
        }

        @Override
        public void update(Map<String, Object> data) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(new JSONObject(data).toJSONString());
                writer.flush();
            } catch (IOException exception) {
                throw new IllegalStateException("Cannot write to: " + file.getPath(), exception);
            }
        }

        @Override
        public void drop() {
            file.delete();
        }
    }
}
