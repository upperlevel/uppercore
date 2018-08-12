package xyz.upperlevel.uppercore.storage.impl;

import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.storage.StorageConnector;
import xyz.upperlevel.uppercore.storage.Storage;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class MySql {
    private MySql() {
    }

    public static StorageConnector storage() {
        return new StorageConnectorImpl();
    }

    /* --------------------------------------------------------------------------------- StorageConnector */
    public static class StorageConnectorImpl extends StorageConnector {
        public StorageConnectorImpl() {
            super("mysql");
        }

        @Override
        public boolean isSupported() {
            return true;
        }

        @Override
        public String[] getDownloadLinks() {
            // MySQL driver already implemented on Spigot
            return new String[0];
        }

        @Override
        public Storage connect(Config access) {
            String url = "jdbc:mysql://" + access.getStringRequired("address") + ":" + access.getIntRequired("port");
            try {
                java.sql.Connection conn;
                if (access.has("username") && access.has("password")) {
                    conn = DriverManager.getConnection(url, access.getString("username"), access.getString("password"));
                } else {
                    conn = DriverManager.getConnection(url);
                }
                return new Sql.StorageImpl(conn);
            } catch (SQLException e) {
                throw new IllegalStateException("Can't connect to MySQL", e);
            }
        }
    }
}
