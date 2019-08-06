package xyz.upperlevel.uppercore.storage;

public interface Table {
    /**
     * Creates the table if it wasn't present yet, returns true only if the table wasn't present before the call
     *
     * @return true if the table has been created, false if it was already present.
     */
    boolean create();

    /**
     * Drops the table with also all of its entries, returns true only if the table has been deleted.
     * If the table was already deleted (or non-present) at the time of the call, the method will return false.
     *
     * @return true only if the table has been deleted by this call.
     */
    boolean drop();

    /**
     * Selects a single element of the table, note that even if the element is not present this method will
     * return an instance of the Element class that is able to control that row.
     * The element will not be downloaded by this call as it only specifies the element to use.
     *
     * @param id the id of the element to select
     * @return an instance able to control the element specified by the id
     */
    Element element(String id);
}
