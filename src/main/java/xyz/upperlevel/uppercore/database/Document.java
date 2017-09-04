package xyz.upperlevel.uppercore.database;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import java.util.Map;

public interface Document {
    Map<String, Object> ask();

    void send(Map<String, Object> data);
}
