package xyz.upperlevel.uppercore.database.mysql;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.upperlevel.uppercore.database.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MySql implements Accessor {
    private final HikariDataSource hikari = new HikariDataSource();

    public MySql() {
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
    }

    @Override
    public void authenticate(Credential credential) {
        hikari.addDataSourceProperty("databaseName", credential.getDatabase());
        hikari.addDataSourceProperty("user", credential.getUsername());
        hikari.addDataSourceProperty("password", credential.getPassword());
    }

    @Override
    public Connection connect() {
        return connect("localhost", 3306);
    }

    @Override
    public Conn connect(String host, int port) {
        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        return new Conn();
    }

    @Data
    public class Conn implements Connection {
        private final java.sql.Connection conn;

        public Conn() {
            try {
                conn = hikari.getConnection();
            } catch (SQLException e) {
                throw new IllegalStateException("Cannot connect to MySQL db: " + e);
            }
        }

        @Override
        public Db database(String id) {
            return new Db(id);
        }

        @Override
        public void disconnect() {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new IllegalStateException("Cannot disconnect from MySQL db: " + e);
            }
        }

        @Data
        public class Db implements Database {
            private final String id;

            public Db(String id) {
                this.id = id;
                try {
                    PreparedStatement stat = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS ?");
                    stat.setString(0, id);
                    stat.execute();
                } catch (SQLException e) {
                    throw new IllegalStateException("Cannot create statement to create db \"" + id + "\": " + e);
                }
            }

            @Override
            public Coll collection(String id) {

                return new Coll(id);
            }

            @Data
            public class Coll implements Collection {
                private final String id;

                public Coll(String id) {
                    this.id = id;
                    try {
                        PreparedStatement stat = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ?(id VARCHAR(36), data JSON)");
                        stat.setString(0, id);
                        stat.execute();
                    } catch (SQLException e) {
                        throw new IllegalStateException("Cannot create statement to create table \"" + id + "\": " + e);
                    }
                }

                @Override
                public Doc document(String id) {
                    return new Doc(id);
                }

                @Data
                public class Doc implements Document {
                    private final String id;

                    public Doc(String id) {
                        this.id = id;
                        try {
                            PreparedStatement stat = conn.prepareStatement("INSERT IGNORE INTO ? VALUES(?, ?)");
                            stat.setString(0, Coll.this.id);
                            stat.setString(1, id);
                            stat.setString(2, new JSONObject().toJSONString());
                            stat.execute();
                        } catch (SQLException e) {
                            throw new IllegalStateException("Cannot create statement to insert new doc \"" + id + "\": " + e);
                        }
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public Map<String, Object> ask() {
                        ResultSet result;
                        try {
                            PreparedStatement stat = conn.prepareStatement("SELECT data FROM ? WHERE id=?");
                            stat.setString(0, Coll.this.id);
                            stat.setString(1, id);
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
                            stat.setString(0, Coll.this.id);
                            stat.setString(1, new JSONObject(data).toJSONString());
                            stat.setString(2, id);
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
