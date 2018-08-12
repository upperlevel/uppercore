package xyz.upperlevel.uppercore.storage.impl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.storage.Database;
import xyz.upperlevel.uppercore.storage.Element;
import xyz.upperlevel.uppercore.storage.Storage;
import xyz.upperlevel.uppercore.storage.Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class Sql {
    private Sql() {
    }

    /* --------------------------------------------------------------------------------- Connection */
    public static class StorageImpl implements Storage {
        private final java.sql.Connection sql;

        public StorageImpl(java.sql.Connection sql) {
            this.sql = sql;
        }

        @Override
        public Database database(String name) {
            try {
                // Can't use ? as schema name
                PreparedStatement statement = sql.prepareStatement("CREATE SCHEMA IF NOT EXISTS `" + name + "`");
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new IllegalStateException("Can't create database (schema): " + name, e);
            }
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
        public Table table(String name) {
            try {
                // Focus database
                PreparedStatement statement = sql.prepareStatement("USE `" + this.name + "`");
                statement.executeUpdate();

                // Create table
                statement = sql.prepareStatement("CREATE TABLE `" + name + "` (`id` VARCHAR(256), `data` JSON)");
                statement.executeUpdate();

                // Add primary key
                statement = sql.prepareStatement("ALTER TABLE `" + name + "` ADD PRIMARY KEY(`id`)");
                statement.executeUpdate();
            } catch (SQLException e) {
                // Table already created? Primary key already added? That's fine
            }
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

        @Override
        public Element element(String id) {
            try {
                PreparedStatement statement = sql.prepareStatement("INSERT INTO `" + database.name + "`.`" + name + "` VALUES (?, '{}')");
                statement.setString(1, id);
                statement.executeUpdate();
            } catch (SQLException ignored) {
                // Error if already inserted, which is fine
            }
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
        public Map<String, Object> getAll() {
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

        @Override
        public void update(Map<String, Object> data) {
            try {
                PreparedStatement statement = sql.prepareStatement("UPDATE `" + table.database.name + "`.`" + table.name + "` SET `data`=?");
                statement.setString(1, new JSONObject(data).toJSONString());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new IllegalStateException("Can't insert element into table: " + table.name, e);
            }
        }

        @Override
        public void drop() {
            try {
                PreparedStatement statement = sql.prepareStatement("DELETE FROM `" + table.database.name + "`.`" + table.name + "` WHERE `id`=?");
                statement.setString(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new IllegalStateException("Can't drop element: " + id);
            }
        }
    }
}
