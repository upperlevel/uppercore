package xyz.upperlevel.uppercore.database.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.database.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySql implements Storage {

    @Override
    public String getId() {
        return "mysql";
    }

    @Override
    public Connection connect(String database) {
        return connect(database, "localhost", 3306);
    }

    @Override
    public Connection connect(String database, String host, int port) {
        try {
            return new ImplConnection(database, host, port);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to mysql database", e);
        }
    }

    @Override
    public Connection connect(String database, String host, int port, String user, String password) {
        try {
            return new ImplConnection(database, host, port, user, password);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to mysql database", e);
        }
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

    public class ImplConnection implements Connection {
        public static final String MYSQL_PREFIX = "jdbc:mysql";
        public static final String IP_STRUCTURE = MYSQL_PREFIX + "://%s:%d/%s";
        private final java.sql.Connection handle;
        private PreparedStatement askStatement;
        private PreparedStatement sendStatement;
        private PreparedStatement createTableStatement;

        public ImplConnection(String database, String host, int port) throws SQLException {
            this.handle = DriverManager.getConnection(String.format(IP_STRUCTURE, host, port, database));
            prepareStatements();
        }

        public ImplConnection(String database, String host, int port, String user, String password) throws SQLException {
            this.handle = DriverManager.getConnection(String.format(IP_STRUCTURE, host, port, database), user, password);
            prepareStatements();
        }

        private void prepareStatements() throws SQLException {
            askStatement = handle.prepareStatement("SELECT VAL FROM ? WHERE ID = ?");
            sendStatement = handle.prepareStatement("REPLACE INTO ? (ID, VAL) VALUES(?, ?);");
            createTableStatement = handle.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ?(" +
                    "ID VARCHAR(16) NOT NULL," +
                    "VAL JSON NOT NULL," +
                    "PRIMARY KEY (ID)" +
                    ");"
            );
        }

        @Override
        public ImplDatabase database() {
            return new ImplDatabase();
        }

        public class ImplDatabase implements Database {

            @Override
            public Table table(String id) {
                try {
                    return new ImplTable(id);
                } catch (SQLException e) {
                    throw new IllegalStateException("Cannot open table " + id, e);
                }
            }

            public class ImplTable implements Table {
                @Getter
                private final String id;

                public ImplTable(String id) throws SQLException {
                    this.id = id;
                    createTableStatement.setString(1, id);
                    createTableStatement.execute();
                }

                @Override
                public Document document(String id) {
                    return new ImplDocument(id);
                }

                @RequiredArgsConstructor
                public class ImplDocument implements Document {
                    @Getter
                    private final String id;

                    @SuppressWarnings("unchecked")
                    @Override
                    public Map<String, Object> ask() {
                        String json;
                        try {
                            askStatement.setString(1, ImplTable.this.id);
                            askStatement.setString(2, this.id);
                            ResultSet res = askStatement.executeQuery();
                            if(res.next()) {
                                json = res.getString(1);
                            } else {
                                json = "";
                            }
                        } catch (SQLException e) {
                            throw new IllegalStateException("Cannot retrieve document changes", e);
                        }

                        JSONParser parser = new JSONParser();
                        try {
                            return (Map<String, Object>) parser.parse(json);
                        } catch (ParseException e) {
                            return new HashMap<>();
                        }
                    }

                    @Override
                    public void send(Map<String, Object> data) {
                        try {
                            sendStatement.setString(1, ImplTable.this.id);
                            sendStatement.setString(2, this.id);
                            sendStatement.setString(3, new JSONObject(data).toJSONString());
                            sendStatement.executeUpdate();
                        } catch (SQLException e) {
                            throw new IllegalStateException("Cannot send document changes", e);
                        }
                    }
                }
            }
        }
    }
}
