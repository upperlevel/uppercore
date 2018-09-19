package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.Node;
import xyz.upperlevel.uppercore.config.exceptions.ConfigException;
import xyz.upperlevel.uppercore.config.exceptions.UnparsableConfigClass;

import java.util.function.Predicate;

public class PolymorphicClassParser<T> extends ConfigParser<T> {
    private final ConstructorConfigParser<Class<? extends T>> classSelector;
    private final ConfigParserRegistry registry;
    private ConstructorConfigParser<T> directParser = null;

    public PolymorphicClassParser(Class<T> clazz, ConstructorConfigParser<Class<? extends T>> classSelector, ConfigParserRegistry registry) {
        super(clazz);
        this.classSelector = classSelector;
        this.registry = registry;
    }


    @Override
    public T parse(Plugin plugin, Node root) {
        Class<?> selectedClass;
        selectedClass = classSelector.parse(plugin, root);
        if (selectedClass == null) {
            throw new ConfigException(root, "Cannot determine subclass");
        }
        ConfigParser<T> parser;
        if (selectedClass != getHandleClass()) {
            // noinspection unchecked
            parser = (ConfigParser<T>) registry.getFor(selectedClass);
        } else {
            // Doing a normal lookup won't be enough as it would return the polymorphic selector (this)
            // so we must do it manually
            parser = getDirectParser();
        }
        if (parser instanceof ConstructorConfigParser && !((ConstructorConfigParser<T>) parser).isSpecial()) {
            // The property name is already used if the selector has used it
            Predicate<String> isPropertyUsedByPolymorphicSelector = classSelector::isUsingArgument;
            // If the selector has already used it then it's normal that the real constructor doesn't have it anymore
            ((ConstructorConfigParser<T>) parser).setIgnoreUnmatchedProperties(isPropertyUsedByPolymorphicSelector);
        }
        return parser.parse(plugin, root);
    }

    public ConstructorConfigParser<T> getDirectParser() {
        if (directParser == null) {
            directParser = registry.setupConstructorParser(getHandleClass());
            if (directParser == null) {
                throw new UnparsableConfigClass(getHandleClass());
            }
        }
        return directParser;
    }
}
