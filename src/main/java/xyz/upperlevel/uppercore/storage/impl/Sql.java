package xyz.upperlevel.uppercore.storage.impl;

import com.google.gson.JsonParseException;
import xyz.upperlevel.uppercore.storage.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static xyz.upperlevel.uppercore.util.JsonUtil.GENERAL_GSON;
import static xyz.upperlevel.uppercore.util.JsonUtil.JSON_MAP_TYPE;

public final class Sql {
    private Sql() {
    }

    private static class StatementBank {
        public PreparedStatement tableCreate;
        public PreparedStatement tableDrop;
        public PreparedStatement elementInsert;
        public PreparedStatement elementReplace;
        public PreparedStatement elementUpdate;
        public PreparedStatement elementDelete;
        public PreparedStatement elementGet;
        public PreparedStatement elementGetParameter;
        public PreparedStatement elementMerge;

        public StatementBank(java.sql.Connection sql, String tableName) throws SQLException {
            this.tableCreate = sql.prepareStatement("CREATE TABLE " + tableName + " (id VARCHAR(256), data JSON NOT NULL, PRIMARY KEY (id))");
            this.tableDrop = sql.prepareStatement("DROP TABLE "  + tableName);
            this.elementInsert = sql.prepareStatement("INSERT INTO " + tableName + " (`id`, `data`) VALUES (?, ?)");
            this.elementReplace = sql.prepareStatement("REPLACE INTO " + tableName + " (`id`, `data`) VALUES (?, ?)");
            this.elementUpdate = sql.prepareStatement("REPLACE INTO " + tableName + " (`id`, `data`) VALUES (?, ?)");
            this.elementDelete = sql.prepareStatement("DELETE FROM " + tableName + " WHERE `id` = ?");
            this.elementGet = sql.prepareStatement("SELECT data FROM " + tableName + " WHERE id = ?");
            this.elementGetParameter = sql.prepareStatement("SELECT `data`->? FROM " + tableName + " WHERE `id` = ?");
            this.elementMerge = sql.prepareStatement("INSERT INTO " + tableName + " (`id`, `data`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `data` = JSON_MERGE_PATCH(`data`, VALUES(`data`))");
        }
    }

    /* --------------------------------------------------------------------------------- Storage */
    public static class StorageImpl implements Storage {
        private final java.sql.Connection sql;

        public StorageImpl(java.sql.Connection sql) {
            this.sql = sql;
        }

        @Override
        public Table table(String name) {
            try {
                return new TableImpl(sql, name);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create prepared statements", e);
            }
        }

        @Override
        public void close() {
            try {
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /* --------------------------------------------------------------------------------- Table */
    public static class TableImpl implements Table {
        private final java.sql.Connection sql;
        private final StatementBank bank;
        private final String name;

        public TableImpl(java.sql.Connection sql, String name) throws SQLException {
            this.sql = sql;
            this.bank = new StatementBank(sql, name);
            this.name = name;
        }

        @Override
        public boolean create() {
            try {
                bank.tableCreate.executeUpdate();
            } catch (SQLException e) {
                return false;
            }
            return true;
        }

        @Override
        public boolean drop() {
            try {
                bank.tableDrop.executeUpdate();
            } catch (SQLException e) {
                return false;
            }
            return true;
        }

        @Override
        public Element element(String id) {
            return new ElementImpl(this, bank, id);
        }
    }

    /* --------------------------------------------------------------------------------- Element */
    public static class ElementImpl implements Element {
        private final StatementBank bank;
        private final TableImpl table;
        private final String id;

        public ElementImpl(TableImpl table, StatementBank bank, String id) {
            this.bank = bank;
            this.table = table;
            this.id = id;
        }

        @Override
        public boolean insert(Map<String, Object> data, DuplicatePolicy duplicatePolicy) {
            try {
                PreparedStatement statement;
                switch (duplicatePolicy) {
                    case KEEP_OLD:
                        statement = bank.elementInsert;
                        break;
                    case REPLACE:
                        statement = bank.elementReplace;
                        break;
                    case MERGE:
                        statement = bank.elementMerge;
                        break;
                    default:
                        throw new IllegalStateException("Unknown duplicate policy " + duplicatePolicy.toString());
                }

                statement.setString(1, id);
                statement.setString(2, GENERAL_GSON.toJson(data));

                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                // We should get an exception only when an element was already
                // present in the db and we weren't replacing it
                if (duplicatePolicy != DuplicatePolicy.KEEP_OLD) {
                    throw new IllegalStateException(e);
                }
                return false;
            }
        }

        @Override
        public boolean drop() {
            try {
                PreparedStatement statement = bank.elementDelete;
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
                PreparedStatement statement = bank.elementGetParameter;
                String encoded = "$." + parameter;
                statement.setString(1, encoded);
                statement.setString(2, id);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return GENERAL_GSON.fromJson(result.getString(encoded), JSON_MAP_TYPE);
                }
                throw new IllegalArgumentException("Parameter '" + parameter + "' not found for element: " + id);
            } catch (SQLException e) {
                throw new IllegalStateException("Can't get element: " + id, e);
            } catch (JsonParseException e) {
                throw new IllegalStateException("Read invalid JSON from element: " + id, e);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Optional<Map<String, Object>> getData() {
            try {

                PreparedStatement statement = bank.elementGet;
                statement.setString(1, id);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return Optional.of(GENERAL_GSON.fromJson(result.getString("data"), JSON_MAP_TYPE));
                }
                return Optional.empty();
            } catch (SQLException e) {
                throw new IllegalStateException("Can't get all the element: " + id, e);
            } catch (JsonParseException e) {
                throw new IllegalStateException("Read invalid JSON from element: " + id, e);
            }
        }
    }
}
