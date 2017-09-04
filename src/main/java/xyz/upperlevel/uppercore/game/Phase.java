package xyz.upperlevel.uppercore.game;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
public interface Phase {

    void onEnable(Phase previous);

    void onDisable(Phase next);
}
