package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.Node;
import xyz.upperlevel.uppercore.config.exceptions.ConfigException;
import xyz.upperlevel.uppercore.config.exceptions.UnparsableConfigType;

import java.util.function.Predicate;

public class PolymorphicClassParser extends ConfigParser {
    private final ConstructorConfigParser<Class<?>> classSelector;
    private final ConfigParserRegistry registry;
    private final Class<?> handleClass;
    private ConstructorConfigParser directParser = null;

    public PolymorphicClassParser(Class<?> clazz, ConstructorConfigParser<Class<?>> classSelector, ConfigParserRegistry registry) {
        super(clazz);
        this.classSelector = classSelector;
        this.registry = registry;
        this.handleClass = clazz;
    }


    @Override
    public Object parse(Plugin plugin, Node root) {
        Class<?> selectedClass;

        selectedClass = classSelector.parse(plugin, root);

        if (selectedClass == null) {
            throw new ConfigException("Cannot determine subclass of " + handleClass.getSimpleName(), root);
        }
        ConfigParser parser;
        if (selectedClass != getHandleClass()) {
            // noinspection unchecked
            parser = registry.getFor(selectedClass);
        } else {
            // Doing a normal lookup won't be enough as it would return the polymorphic selector (this)
            // so we must do it manually
            parser = getDirectParser();
        }
        if (parser instanceof ConstructorConfigParser && !((ConstructorConfigParser) parser).isSpecial()) {
            // The property name is already used if the selector has used it
            Predicate<String> isPropertyUsedByPolymorphicSelector = classSelector::isUsingArgument;
            // If the selector has already used it then it's normal that the real constructor doesn't have it anymore
            ((ConstructorConfigParser) parser).setIgnoreUnmatchedProperties(isPropertyUsedByPolymorphicSelector);
        }
        return parser.parse(plugin, root);
    }

    public ConstructorConfigParser getDirectParser() {
        if (directParser == null) {
            directParser = registry.setupConstructorParser(handleClass);
            if (directParser == null) {
                throw new UnparsableConfigType(getHandleClass());
            }
        }
        return directParser;
    }
}
