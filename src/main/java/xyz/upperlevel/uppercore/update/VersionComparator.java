package xyz.upperlevel.uppercore.update;

public interface VersionComparator {
    Result compare(String curr, String other);

    enum Result {
        /**
         * other is newer than curr.
         */
        NEWER,
        /**
         * The versions passed as parameter are the same
         */
        SAME,
        /**
         * other is older than curr
         */
        OLDER
    }
}
