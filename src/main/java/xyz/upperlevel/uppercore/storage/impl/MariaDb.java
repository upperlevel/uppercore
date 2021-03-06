package xyz.upperlevel.uppercore.storage.impl;

import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.StorageConnector;
import xyz.upperlevel.uppercore.storage.Storage;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class MariaDb  {
    private MariaDb() {
    }

    public static StorageConnector storage() {
        return new StorageConnectorImpl();
    }

    /* --------------------------------------------------------------------------------- StorageConnector */
    public static class StorageConnectorImpl extends StorageConnector {
        public StorageConnectorImpl() {
            super("mariadb");
        }

        @Override
        public boolean isSupported() {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {
                return false;
            }
            return true;
        }

        @Override
        public String[] getDownloadLinks() {
            return new String[] {
                    "https://oss.sonatype.org/content/repositories/releases/org/mariadb/jdbc/mariadb-java-client/2.6.0/mariadb-java-client-2.6.0.jar"
            };
        }

        @Override
        public Storage connect(Config access) {
            String url = "jdbc:mariadb://" + access.getStringRequired("address") + ":" + access.getInt("port", 3306) + "/" + access.getStringRequired("database");
            try {
                java.sql.Connection conn;
                if (access.has("username") && access.has("password")) {
                    conn = DriverManager.getConnection(url, access.getString("username"), access.getString("password"));
                } else {
                    conn = DriverManager.getConnection(url);
                }
                return new Sql.StorageImpl(conn);
            } catch (SQLException e) {
                throw new IllegalStateException("Can't connect to MariaDB", e);
            }
        }
    }
}
