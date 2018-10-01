package xyz.upperlevel.uppercore.storage.impl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
            this.folder =  new File("plugins", name + File.separator + "db");
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
        public boolean insert(Map<String, Object> data, boolean replace) {
            if (replace) {
                file.delete();
            }
            if (file.exists()) {
                return false;
            } else {
                try (FileWriter writer = new FileWriter(file)) {
                    file.createNewFile();
                    writer.write(new JSONObject(data).toJSONString());
                    writer.flush();
                } catch (IOException e) {
                    throw new IllegalStateException("Can't write to file: " + file, e);
                }
            }
            return true;
        }

        @Override
        public boolean update(Map<String, Object> data) {
            Map<String, Object> newData = getData();
            newData.putAll(data);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(new JSONObject(newData).toJSONString());
                writer.flush();
                return true;
            } catch (IOException e) {
                throw new IllegalStateException("Can't write to file: " + file.getPath(), e);
            }
        }

        @Override
        public Object get(String parameter) {
            String prev = null;
            Object tree = getData();
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
        public Map<String, Object> getData() {
            JSONParser parser = new JSONParser();
            try {
                return (Map<String, Object>) parser.parse(new FileReader(file));
            } catch (IOException | ParseException e) {
                throw new IllegalStateException("Can't read element file: " + file.getPath(), e);
            }
        }

        @Override
        public boolean drop() {
            return file.delete();
        }
    }
}
