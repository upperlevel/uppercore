package xyz.upperlevel.uppercore.storage;

import xyz.upperlevel.uppercore.config.Config;

import java.util.Map;

public interface Element {
    /**
     * Sends the given data remotely on this element.
     *
     * @param data the data to insert
     * @param policy what to do when a duplicate is found while inserting
     *
     * @return {@code true} if inserted, otherwise {@code false}.
     */
    boolean insert(Map<String, Object> data, DuplicatePolicy policy);

    /**
     * Updates the existing data with the given one.
     * If a parameter is found in both, the remote one is replaced.
     * If a parameter is present only in the new data given, it'll be added.
     *
     * @param data the update data
     *
     * @return {@code true} if something got updated, otherwise {@code false}.
     */
    boolean update(Map<String, Object> data);

    /**
     * Gets the value of a remote parameter.
     *
     * @param parameter the parameter name (nested parameters names follows the format: {@code "first.second.third"})
     * @return the parameter value
     */
    Object get(String parameter);

    /**
     * Gets all the remote data related to this element.
     *
     * @return the data
     */
    Map<String, Object> getData();

    default Config asConfig() {
        return Config.from(getData());
    }

    /**
     * Deletes the remote element.
     *
     * @return {@code true} if found, otherwise {@code false}
     */
    boolean drop();
}
