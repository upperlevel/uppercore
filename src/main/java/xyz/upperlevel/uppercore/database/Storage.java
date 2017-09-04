package xyz.upperlevel.uppercore.database;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */


public interface Storage {
    String getId();

    Connection connect(String database);

    Connection connect(String database, String host, int port);

    Connection connect(String database, String host, int port, String user, String password);
}
