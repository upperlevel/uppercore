package xyz.upperlevel.uppercore.database.mongodb;

import xyz.upperlevel.uppercore.database.Driver;

public class MongoDbDriver extends Driver {
    public MongoDbDriver() {
        super("mongodb");
    }

    @Override
    protected MongoDb accessor() {
        return new MongoDb();
    }
}
