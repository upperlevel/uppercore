package xyz.upperlevel.uppercore.database.rethinkdb;

import xyz.upperlevel.uppercore.database.Driver;

public class RethinkDbDriver extends Driver {
    public RethinkDbDriver() {
        super("rethinkdb");
    }

    @Override
    protected RethinkDb accessor() {
        return new RethinkDb();
    }
}
