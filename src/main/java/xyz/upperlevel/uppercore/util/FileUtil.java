package xyz.upperlevel.uppercore.util;

import java.io.File;

public final class FileUtil {
    public static String getName(String nameWithExtension) {
        int lastDotIndex = nameWithExtension.lastIndexOf('.');
        if (lastDotIndex < 0) return nameWithExtension;
        return nameWithExtension.substring(0, lastDotIndex);
    }

    public static String getName(File file) {
        return getName(file.getName());
    }

    public String getExtension(String nameWithExtension) {
        int lastDotIndex = nameWithExtension.lastIndexOf('.');
        if (lastDotIndex < 0) return "";
        return nameWithExtension.substring(lastDotIndex + 1);
    }

    public String getExtension(File file) {
        return getExtension(file.getName());
    }

    private FileUtil(){}
}
