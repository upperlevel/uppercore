package xyz.upperlevel.uppercore.storage;

public interface Database {
    /**
     * Creates the database if it hasn't been yet created, returns true if any changes were made
     * (so if the database was not present before the call).
     *
     * @return true if the database has been created, false if it was already present.
     */
    boolean create();

    /**
     * Deletes the database if it was present. It will also delete its tables and entries.
     *
     * @return true if the database was present and it has been succesfully deleted
     */
    boolean drop();

    /**
     * Gets the instance of the Table with the name as supplied in the parameter, this does not
     * however create anything, to create it use the create method inside of the returned instance.
     * This method returns even if the table isn't present (yet).
     *
     * @param name The name of the table to get
     * @return An instance of the table
     */
    Table table(String name);
}
