package xyz.upperlevel.uppercore.database.flatfile;

import xyz.upperlevel.uppercore.database.Driver;

public class FlatfileDriver extends Driver{
    public FlatfileDriver() {
        super("flatfile");
    }

    @Override
    protected Flatfile accessor() {
        return new Flatfile();
    }
}
