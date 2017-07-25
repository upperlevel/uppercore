package xyz.upperlevel.uppercore;

import xyz.upperlevel.uppercore.config.Config;

public interface Loader<T> {

    T load(Config config);
}
