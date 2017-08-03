package xyz.upperlevel.uppercore.util.nms;

import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;

public interface Nms {

    void initialize() throws Exception;

    default void load() {
        try {
            initialize();
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    static void handleException(Exception e) {
        throw new UnsupportedVersionException(e);
    }
}
