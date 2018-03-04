package xyz.upperlevel.uppercore.database.impl;

import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.database.Database;
import xyz.upperlevel.uppercore.database.Document;
import xyz.upperlevel.uppercore.database.Storage;
import xyz.upperlevel.uppercore.database.Table;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySql extends Storage {
    public MySql() {
        super("mysql");
    }

    @Override
    public boolean isSupported() {
        // For json encoding we use json.simple that is supported even in 1.8, so it should always be supported
        // The MySQL drivers should be in the bukkit JAR from a long time too
        return true;
    }

    @Override
    public String[] getDownloadLinks() {
        return new String[0];
    }

    @Override
    public Database onConnect(String address, int port, String database, String password, String username) {
        // Connection
        String url = "jdbc:mysql://" + address + ":" + port + "/" + database;
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot connect to: " + url);
        }
        return new DatabaseImpl(connection);
    }

    // Database
    public class DatabaseImpl implements Database {
        private final Connection connection;
        private PreparedStatement askQuery, sendQuery, createTableQuery;

        private void prepareStatements() {
            try {
                askQuery = connection.prepareStatement("SELECT value FROM ? WHERE id = ?;");
                sendQuery = connection.prepareStatement("REPLACE INTO ? (id, value) VALUES(?, ?);");
                createTableQuery = connection.prepareStatement("CREATE TABLE IF NOT EXISTS ?(id varchar(16), value JSON);");
            } catch (SQLException exception) {
                throw new IllegalStateException("Cannot init statements: ", exception);
            }
        }

        public DatabaseImpl(Connection connection) {
            this.connection = connection;
            prepareStatements();
        }

        @Override
        public Table table(String id) {
            try {
                createTableQuery.setString(1, id);
                createTableQuery.execute();
            } catch (SQLException exception) {
                throw new IllegalStateException("Cannot create table: " + id, exception);
            }
            return new TableImpl(id);
        }

        // Table
        public class TableImpl implements Table {
            @Getter
            private final String tableId;

            public TableImpl(String tableId) {
                this.tableId = tableId;
            }

            @Override
            public Document document(String id) {
                return new DocumentImpl(id);
            }

            // Document
            public class DocumentImpl implements Document {
                private final String documentId;

                public DocumentImpl(String documentId) {
                    this.documentId = documentId;
                }

                @SuppressWarnings("unchecked")
                @Override
                public Map<String, Object> ask() {
                    String json;
                    try {
                        askQuery.setString(1, tableId);
                        askQuery.setString(2, documentId);
                        ResultSet result = askQuery.executeQuery();

                        if (result.next()) {
                            json = result.getString(1);
                        } else {
                            json = "";
                        }
                    } catch (SQLException exception) {
                        throw new IllegalStateException("Cannot get document: id=" + documentId, exception);
                    }
                    try {
                        return (Map<String, Object>) new JSONParser().parse(json);
                    } catch (ParseException e) {
                        return new HashMap<>();
                    }
                }

                @Override
                public void send(Map<String, Object> data) {
                    try {
                        sendQuery.setString(1, tableId);
                        sendQuery.setString(2, documentId);
                        sendQuery.setString(3, new JSONObject(data).toJSONString());
                        sendQuery.executeUpdate();
                    } catch (SQLException exception) {
                        throw new IllegalStateException("Cannot send document: id=" + documentId, exception);
                    }
                }
            }
        }
    }
}
