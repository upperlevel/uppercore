package xyz.upperlevel.uppercore.database.mysql;

import xyz.upperlevel.uppercore.database.Driver;

public class MySqlDriver extends Driver {
    public MySqlDriver() {
        super("mysql");
    }

    @Override
    protected MySql accessor() {
        return new MySql();
    }
}
