package xyz.upperlevel.uppercore.storage;

public interface Table {
    /**
     * Gets the element by id. If not present creates it.
     */
    Element element(String id);
}
