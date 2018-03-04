package xyz.upperlevel.uppercore.database;

public class StorageNotSupportedException extends RuntimeException {
    public StorageNotSupportedException(Storage storage) {
        super("The storage type '" + storage.getName() + "' cannot find its required files");
    }
}
