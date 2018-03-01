package xyz.upperlevel.uppercore.nms.exceptions;

import static xyz.upperlevel.uppercore.nms.NmsVersion.VERSION;

public class UnsupportedVersionException extends RuntimeException {

    public UnsupportedVersionException(Exception e) {
        super("Unsupported version \"" + VERSION + "\", report this to the developers", e);
    }

    public String getVersion() {
        return VERSION;
    }
}
