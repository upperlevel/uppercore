package xyz.upperlevel.uppercore.database.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.database.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        HikariDataSource pool = new HikariDataSource();
        pool.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        pool.addDataSourceProperty("serverName", host);
        pool.addDataSourceProperty("port", port);
        pool.addDataSourceProperty("databaseName", database);
        try {
            return new ImplConnection(database, pool.getConnection());
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to " + host + ":" + port + " with db=" + database + ": " + e);
        }
    }

    @Override
    public Connection connect(String database, String host, int port, String user, String password) {
        HikariDataSource pool = new HikariDataSource();
        pool.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        pool.addDataSourceProperty("serverName", host);
        pool.addDataSourceProperty("port", port);
        pool.addDataSourceProperty("databaseName", database);
        pool.addDataSourceProperty("user", user);
        pool.addDataSourceProperty("password", password);
        try {
            return new ImplConnection(database, pool.getConnection());
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to " + host + ":" + port + " with db=" + database + " user=" + user + " password=" + password + ": " + e);
        }
    }

    @Data
    public class ImplConnection implements Connection {
        private final String db;
        private final java.sql.Connection conn;

        @Override
        public ImplDatabase database() {
            return new ImplDatabase();
        }

        @Getter
        public class ImplDatabase implements Database {
            @Override
            public ImplTable table(String id) {
                try {
                    // create
                    Statement stat = conn.createStatement();
                    stat.executeUpdate("CREATE TABLE " + id + " (id VARCHAR(36), data VARCHAR)");
                    //
                    return new ImplTable(id);
                } catch (SQLException e) {
                    throw new IllegalStateException("Cannot create statement to create table \"" + id + "\": " + e);
                }
            }

            @Getter
            public class ImplTable implements Table {
                private final String id;

                public ImplTable(String id) {
                    this.id = id;
                }

                @Override
                public ImplDocument document(String id) {
                    try {
                        // create
                        PreparedStatement stat = conn.prepareStatement("INSERT IGNORE INTO ? VALUES(?, ?)");
                        stat.setString(1, ImplTable.this.id);
                        stat.setString(2, id);
                        stat.setString(3, new JSONObject().toJSONString());
                        stat.execute();
                        //
                        return new ImplDocument(id);
                    } catch (SQLException e) {
                        throw new IllegalStateException("Cannot create statement to insert new doc \"" + id + "\": " + e);
                    }
                }

                @Data
                public class ImplDocument implements Document {
                    private final String id;

                    public ImplDocument(String id) {
                        this.id = id;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public Map<String, Object> ask() {
                        ResultSet result;
                        try {
                            PreparedStatement stat = conn.prepareStatement("SELECT data FROM ? WHERE id=?");
                            stat.setString(1, ImplTable.this.id);
                            stat.setString(2, id);
                            result = stat.executeQuery();
                            if (result.next()) {
                                JSONParser parser = new JSONParser();
                                return (JSONObject) parser.parse(result.getString("data"));
                            }
                            return null;
                        } catch (SQLException e) {
                            throw new IllegalStateException("Cannot get json data from \"" + id + "\": " + e);
                        } catch (ParseException e) {
                            throw new IllegalStateException("Cannot parse received json: " + e);
                        }
                    }

                    @Override
                    public void send(Map<String, Object> data) {
                        try {
                            PreparedStatement stat = conn.prepareStatement("UPDATE ? SET data=? WHERE id=?");
                            stat.setString(1, ImplTable.this.id);
                            stat.setString(2, new JSONObject(data).toJSONString());
                            stat.setString(3, id);
                            new JSONObject(data);
                        } catch (SQLException e) {
                            throw new IllegalStateException("Cannot update json data from \"" + id + "\": " + e);
                        }
                    }
                }
            }
        }
    }
}
