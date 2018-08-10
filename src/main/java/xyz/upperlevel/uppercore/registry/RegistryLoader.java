package xyz.upperlevel.uppercore.registry;

import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.parser.ConfigParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public interface RegistryLoader<T> {
    T load (Registry parent, String id, Reader in);

    static <T> RegistryLoader<T> fromClass(Class<T> clazz) {
        ConfigParser<T> parser = Uppercore.parsers().getFor(clazz);
        return (parent, id, in) -> parser.parse(parent.getPlugin(), in);
    }
}
