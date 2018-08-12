package xyz.upperlevel.uppercore.storage;

public interface Storage {
    /**
     * Creates or gets database by name.
     */
    Database database(String name);
}
