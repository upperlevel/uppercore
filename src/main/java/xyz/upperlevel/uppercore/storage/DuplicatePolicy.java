package xyz.upperlevel.uppercore.storage;

public enum DuplicatePolicy {
    /**
     * If another entry is already in the database then the one that is already present will be kept
     * and the database will not be changed.
     */
    KEEP_OLD,
    /**
     * The entry inserted will replace any older entries if present, so the database will always be modified.
     */
    REPLACE,
    /**
     * If a data row is already present updates the existing data with the given one.
     * If a parameter is found in both, the one from the new entry will take precedence.
     * If a parameter is present only in the given entry, it'll be added.
     * This is not a deep operation hence this will treat map values like normal objects, replacing them.
     */
    MERGE,
}
