package xyz.upperlevel.uppercore.storage;

import xyz.upperlevel.uppercore.config.Config;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public interface Element {
    /**
     * Changes the data of this element present on the database using the DuplicatePolicy for merge problems.
     *
     * @param data the data to insert
     * @param policy what to do when a duplicate is found while inserting
     *
     * @return {@code true} if inserted, otherwise {@code false}.
     */
    boolean insert(Map<String, Object> data, DuplicatePolicy policy);

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
    Optional<Map<String, Object>> getData();

    default Optional<Config> asConfig() {
        return getData().map(Config::from);
    }

    /**
     * Deletes the remote element.
     *
     * @return {@code true} if found, otherwise {@code false}
     */
    boolean drop();
}
