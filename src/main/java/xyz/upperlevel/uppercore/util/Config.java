package xyz.upperlevel.uppercore.util;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public interface Config {

    Object get(String key);

    default String getString(String key) {
        return (String) get(key);
    }

    default byte getByte(String key) {
        return (byte) get(key);
    }

    default short getShort(String key) {
        return (short) get(key);
    }

    default int getInt(String key) {
        return (int) get(key);
    }

    default long getLong(String key) {
        return (long) get(key);
    }

    default float getFloat(String key) {
        return (float) ((double) get(key));
    }

    default double getDouble(String key) {
        return (double) get(key);
    }

    default Location getLocation(String key) {
        return SerializationUtil.deserializeLocation(getSection(key));
    }

    default List<Location> getLocationList(String key) {
        List<Location> result = new ArrayList<>();
        List<Map<String, Object>> data = getMapList(key);
        for (Map<String, Object> location : data)
            result.add(SerializationUtil.deserializeLocation(location::get));
        return result;
    }

    default Config getSection(String key) {
        return ((Map<String, Object>) get(key))::get;
    }

    default <T> List<T> getList(String key) {
        return (List<T>) get(key);
    }

    default List<String> getStringList(String key) {
        return (List<String>) get(key);
    }

    default List<Map<String, Object>> getMapList(String key) {
        return (List<Map<String, Object>>) get(key);
    }

    static Config wrap(Map<String, Object> map) {
        return map::get;
    }

    static Config wrap(ConfigurationSection section) {
        return section::get;
    }
}
