package xyz.upperlevel.uppercore.storage.impl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.storage.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class Sql {
    private Sql() {
    }

    /* --------------------------------------------------------------------------------- Storage */
    public static class StorageImpl implements Storage {
        private final java.sql.Connection sql;

        public StorageImpl(java.sql.Connection sql) {
            this.sql = sql;
        }

        @Override
        public Database database(String name) {
            return new DatabaseImpl(sql, name);
        }
    }

    /* --------------------------------------------------------------------------------- Database */
    public static class DatabaseImpl implements Database {
        private final java.sql.Connection sql;
        private final String name;

        public DatabaseImpl(java.sql.Connection sql, String name) {
            this.sql = sql;
            this.name = name;
        }

        @Override
        public boolean create() {
            try {
                PreparedStatement statement = sql.prepareStatement("CREATE DATABASE `" + name + "`");
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        public boolean drop() {
            try {
                PreparedStatement statement = sql.prepareStatement("DROP DATABASE `" + name + "`");
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        public Table table(String name) {
            return new TableImpl(this, name);
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final java.sql.Connection sql;
        private final DatabaseImpl database;
        private final String name;

        public TableImpl(DatabaseImpl database, String name) {
            this.sql = database.sql;
            this.database = database;
            this.name = name;
        }

        private String getPath() {
            return "`" + database.name + "`.`" + name + "`";
        }

        @Override
        public boolean create() {
            try {
                PreparedStatement statement = sql.prepareStatement("USE `" + database.name + "`");
                statement.executeUpdate();

                statement = sql.prepareStatement("CREATE TABLE `" + name + "` (`id` VARCHAR(256), `data` JSON)");
                statement.executeUpdate();

                statement = sql.prepareStatement("ALTER TABLE " + getPath() + " ADD PRIMARY KEY(`id`)");
                statement.executeUpdate();
            } catch (SQLException e) {
                return false;
            }
            return true;
        }

        @Override
        public boolean drop() {
            try {
                PreparedStatement statement = sql.prepareStatement("DROP TABLE " + getPath());
                statement.executeUpdate();
            } catch (SQLException e) {
                return false;
            }
            return true;
        }

        @Override
        public Element element(String id) {
            return new ElementImpl(this, id);
        }
    }

    /* --------------------------------------------------------------------------------- Element */
    public static class ElementImpl implements Element {
        private final java.sql.Connection sql;
        private final TableImpl table;
        private final String id;

        public ElementImpl(TableImpl table, String id) {
            this.sql = table.sql;
            this.table = table;
            this.id = id;
        }

        @Override
        public boolean insert(Map<String, Object> data, DuplicatePolicy duplicatePolicy) {
            try {
                String query = "INSERT INTO " + table.getPath() + " (`id`, `data`) VALUES (?, '{}')";
                if (duplicatePolicy == DuplicatePolicy.REPLACE) {
                    query += "ON DUPLICATE KEY UPDATE `data`=?";
                }
                PreparedStatement statement = sql.prepareStatement(query);
                statement.setString(1, id);
                if (duplicatePolicy == DuplicatePolicy.REPLACE) {
                    statement.setString(2, new JSONObject(data).toJSONString());
                }
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                // We should get an exception only when an element was already
                // present in the db and we weren't replacing it
                if (duplicatePolicy == DuplicatePolicy.REPLACE) {
                    throw new IllegalStateException(e);
                }
                return false;
            }
        }

        @Override
        public boolean update(Map<String, Object> data) {
            try {
                PreparedStatement statement = sql.prepareStatement("UPDATE `" + table.database.name + "`.`" + table.name + "` SET `data`=?");
                statement.setString(1, new JSONObject(data).toJSONString());
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public boolean drop() {
            try {
                PreparedStatement statement = sql.prepareStatement("DELETE FROM `" + table.database.name + "`.`" + table.name + "` WHERE `id`=?");
                statement.setString(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                return false;
            }
            return true;
        }

        @Override
        public Object get(String parameter) {
            try {
                PreparedStatement statement = sql.prepareStatement("SELECT `data`->? FROM `" + table.database.name + "`.`" + table.name + "` WHERE `id`=?");
                String encoded = "$." + parameter;
                statement.setString(1, encoded);
                statement.setString(2, id);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new JSONParser().parse(result.getString(encoded));
                }
                throw new IllegalArgumentException("Parameter '" + parameter + "' not found for element: " + id);
            } catch (SQLException e) {
                throw new IllegalStateException("Can't get element: " + id, e);
            } catch (ParseException e) {
                throw new IllegalStateException("Read invalid JSON from element: " + id, e);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map<String, Object> getData() {
            try {
                PreparedStatement statement = sql.prepareStatement("SELECT `data` FROM `" + table.database.name + "`.`" + table.name + "`");
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return (Map<String, Object>) new JSONParser().parse(result.getString("data"));
                }
                throw new IllegalStateException("Element not found: " + id);
            } catch (SQLException e) {
                throw new IllegalStateException("Can't get all the element: " + id, e);
            } catch (ParseException e) {
                throw new IllegalStateException("Read invalid JSON from element: " + id, e);
            }
        }
    }
}
