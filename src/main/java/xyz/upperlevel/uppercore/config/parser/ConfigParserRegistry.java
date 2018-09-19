package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigExternalDeclarator;
import xyz.upperlevel.uppercore.config.PolymorphicSelector;
import xyz.upperlevel.uppercore.config.StandardExternalDeclarator;
import xyz.upperlevel.uppercore.config.exceptions.UnparsableConfigClass;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

import static xyz.upperlevel.uppercore.util.GenericUtil.extractClassFromType;
import static xyz.upperlevel.uppercore.util.GenericUtil.getGenericChildren;

public class ConfigParserRegistry {
    private static final SafeConstructor.ConstructYamlTimestamp TIMESTAMP_CONSTRUCTOR = new SafeConstructor.ConstructYamlTimestamp();
    private static final ConfigParserRegistry standard = createStandard();
    private Map<Class<?>, ParserFactory> parsersByClass = new HashMap<>();

    public <T> void register(Class<T> clazz, ParserFactory factory) {
        parsersByClass.put(clazz, factory);
    }

    public <T> void register(Class<T> clazz, ConfigParser<T> parser) {
        parsersByClass.put(clazz, t -> parser);
    }

    public <T> void register(Class<T> clazz, Function<Node, T> parser) {
        parsersByClass.put(clazz,t -> new ConfigParser<T>(clazz) {
            @Override
            public T parse(Plugin plugin, Node root) {
                return parser.apply(root);
            }
        });
    }

    public <T> void register(Class<T> clazz, Function<String, T> parser, Tag... expectedTags) {
        parsersByClass.put(clazz, t -> new ConfigParser<T>(clazz) {
            @Override
            public T parse(Plugin plugin, Node root) {
                checkTag(root, Arrays.asList(expectedTags));
                return parser.apply(((ScalarNode)root).getValue());
            }
        });
    }

    public void registerFromDeclarator(ConfigExternalDeclarator declarator) {
        ConstructorConfigParser.loadFromDeclarator(declarator, this);
    }

    public <T> ConfigParser<T> setupPolyphormicClassParser(Class<T> clazz) {
        Method selector = null;
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PolymorphicSelector.class)) {
                continue;
            }
            if (selector != null) {
                throw new IllegalArgumentException("Multiple @PolymorphicSelector found in " + clazz.getName());
            }

            if ((method.getModifiers() & Modifier.STATIC) == 0) {
                throw new IllegalArgumentException("@PolymorphicSelector method MUST be static (" + clazz.getName() + "#" + method.getName() + ")");
            }
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1 && parameters[0] != Node.class) {
                throw new IllegalArgumentException("Illegal arguments in @PolymorphicSelector (" + clazz.getName() + "#" + method.getName() + ")");
            }
            if (method.getReturnType() != Class.class) {
                throw new IllegalArgumentException("Return type of @PolymorphicSelector must be Class (" + clazz.getName() + "#" + method.getName() + ")");
            }
            method.setAccessible(true);
            selector = method;
        }
        if (selector == null) {
            return null;
        }
        final Method finalSelector = selector;
        /*
         * This needs a little bit of explaination:
         * we're hacking the ConstructorConfigParser and letting him manage the arguments of the PolymorphicSelector
         * so we're using the PolymorphicSelector as a constructor of Class<?>
         * (yeah it's a hacky way to do it but it should work)
         */
        // noinspection unchecked
        ConstructorConfigParser<Class<? extends T>> classSelector = new ConstructorConfigParser(
                Class.class, this, selector.getParameters(), args -> finalSelector.invoke(null, args), true);

        classSelector.setIgnoreAllUnmatchedProperties(true);

        return new PolymorphicClassParser<>(clazz, classSelector, this);
    }

    public <T> ConstructorConfigParser<T> setupConstructorParser(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        Constructor<T> targetConstructor = null;
        for (Constructor<T> constructor : constructors) {
            if (constructor.isAnnotationPresent(ConfigConstructor.class)) {
                if (targetConstructor != null) {
                    throw new IllegalStateException("Multiple ConfigConstructors in class " + clazz.getName());
                }
                targetConstructor = constructor;
            }
        }
        if (targetConstructor == null) {
            return null;
        }
        targetConstructor.setAccessible(true);
        Parameter[] parameters = targetConstructor.getParameters();
        ConstructorConfigParser.ObjectConstructor<T> refinedConstructor = targetConstructor::newInstance;
        boolean inlineable = targetConstructor.getAnnotation(ConfigConstructor.class).inlineable();
        return new ConstructorConfigParser<>(clazz, this, parameters, refinedConstructor, inlineable);
    }

    public <T> ConfigParser<T> setupClassParser(Class<T> clazz) {
        if (clazz.isEnum()) {
            @SuppressWarnings("unchecked")
            ConfigParser<T> parser = new EnumConfigParser(clazz);
            return parser;
        }
        ConfigParser<T> res = setupPolyphormicClassParser(clazz);
        if (res == null) {
            res = setupConstructorParser(clazz);
        }
        return res;
    }

    public <T> ConfigParser<T> getFor(Class<T> clazz, Type type) {
        // Arrays have their own parsing method
        if (clazz.isArray()) {
            // I hate arrays
            return new ArrayParser<>(clazz, type, this);
        }
        // Check in the known classes (something akin to a cache)
        ParserFactory factory = parsersByClass.get(clazz);
        if (factory != null) {
            // Class already known, return it
            @SuppressWarnings("unchecked")
            ConfigParser<T> parser = factory.create(type);
            return parser;
        }
        // Try to create a parser for the class in runtime
        ConfigParser<T> classParser = setupClassParser(clazz);
        if (classParser == null) {
            // Cannot parse class
            throw new UnparsableConfigClass(clazz);
        }
        // Register in the known types cache
        parsersByClass.put(clazz, t -> classParser);
        return classParser;
    }

    public <T> ConfigParser<T> getFor(Class<T> clazz) {
        return getFor(clazz, clazz);
    }

    public ConfigParser getForGeneric(Type type) {
        Class<?> valClass = extractClassFromType(type);
        return getFor(valClass, type);
    }

    public void registerStandard() {
        registerPrimitives();
        registerCollections();
        registerPlaceholders();
        registerFromDeclarator(new StandardExternalDeclarator());
    }


    public void registerPrimitives() {
        register(Byte.class, Byte::parseByte, Tag.INT);
        register(Short.class, Short::parseShort, Tag.INT);
        register(Integer.class, Integer::parseInt, Tag.INT);
        register(Long.class, Long::parseLong, Tag.INT);
        register(BigInteger.class, BigInteger::new, Tag.INT);
        register(Float.class, Float::parseFloat, Tag.INT, Tag.FLOAT);
        register(Double.class, Double::parseDouble, Tag.INT, Tag.FLOAT);
        register(BigDecimal.class, BigDecimal::new, Tag.INT, Tag.FLOAT);
        register(Boolean.class, new ConfigParser<Boolean>(Boolean.class) {
            @Override
            public Boolean parse(Plugin plugin, Node root) {
                checkTag(root, Tag.BOOL);
                ScalarNode s = (ScalarNode) root;
                switch (s.getValue().toLowerCase()) {
                    case "yes":
                    case "on":
                    case "true":
                        return true;
                    case "no":
                    case "off":
                    case "false":
                        return false;
                }
                throw new WrongValueConfigException(s, s.getValue(), "boolean");
            }
        });
        register(String.class, Function.identity(), Tag.STR);
        register(Character.class, new ConfigParser<Character>(Character.class) {
            @Override
            public Character parse(Plugin plugin, Node root) {
                checkTag(root, Tag.STR);
                ScalarNode n = (ScalarNode) root;
                String s = n.getValue();
                if (s.length() == 0) return null;
                if (s.length() != 1) throw new WrongValueConfigException(n, s, "character");
                return s.charAt(0);
            }
        });
        register(Date.class, new ConfigParser<Date>(Date.class) {
            @Override
            public Date parse(Plugin plugin, Node root) {
                checkTag(root, Tag.TIMESTAMP);
                return (Date) TIMESTAMP_CONSTRUCTOR.construct(root);
            }
        });
        register(Calendar.class, new ConfigParser<Calendar>(Calendar.class) {
            @Override
            public Calendar parse(Plugin plugin, Node root) {
                checkTag(root, Tag.TIMESTAMP);
                TIMESTAMP_CONSTRUCTOR.construct(root);
                return TIMESTAMP_CONSTRUCTOR.getCalendar();
            }
        });
        register(UUID.class, UUID::fromString, Tag.STR);

        // In physics they have the wave-particle duality
        // In java we have the primitive-class duality -_-
        // Aliases handle it just fine, java.lang.reflect will handle the real translation
        parsersByClass.put(Byte.TYPE, parsersByClass.get(Byte.class));
        parsersByClass.put(Short.TYPE, parsersByClass.get(Short.class));
        parsersByClass.put(Integer.TYPE, parsersByClass.get(Integer.class));
        parsersByClass.put(Long.TYPE, parsersByClass.get(Long.class));
        parsersByClass.put(Float.TYPE, parsersByClass.get(Float.class));
        parsersByClass.put(Double.TYPE, parsersByClass.get(Double.class));
        parsersByClass.put(Character.TYPE, parsersByClass.get(Character.class));
        parsersByClass.put(Boolean.TYPE, parsersByClass.get(Boolean.class));
    }

    @SuppressWarnings("unchecked")
    public void registerCollections() {
        register(Set.class, (Type t) -> new CollectionParser<>(Set.class, HashSet::new, getForGeneric(getGenericChildren(t, 0)), Tag.SET));
        register(List.class, (Type t) -> new CollectionParser<>(List.class, ArrayList::new, getForGeneric(getGenericChildren(t, 0)), Tag.SEQ));
        register(EnumSet.class, (Type t) -> {
            Type childrenType = getGenericChildren(t, 0);
            Class childrenClass = extractClassFromType(childrenType);
            return new CollectionParser<>(EnumSet.class, () -> EnumSet.noneOf(childrenClass), getFor(childrenClass, childrenType), Tag.SEQ);
        });
        // TODO: comment
        register(Map.class, (Type t) -> new MapParser<>(Map.class, HashMap::new, getForGeneric(getGenericChildren(t, 0)), getForGeneric(getGenericChildren(t, 1))));
        register(EnumMap.class, (Type t) -> {
            Class keyClass = extractClassFromType(getGenericChildren(t, 0));
            return new MapParser<>(EnumMap.class, () -> new EnumMap(keyClass), getForGeneric(getGenericChildren(t, 0)), getForGeneric(getGenericChildren(t, 1)));
        });
    }

    public void registerPlaceholders() {
        register(PlaceholderValue.class, (Type t) -> new PlaceholderValueParser(getForGeneric(getGenericChildren(t))));
    }

    public interface ParserFactory {
        ConfigParser create(Type type);
    }

    public static ConfigParserRegistry createEmpty() {
        return new ConfigParserRegistry();
    }

    public static ConfigParserRegistry createStandard() {
        ConfigParserRegistry registry = new ConfigParserRegistry();
        registry.registerStandard();// Setup standard types
        return registry;
    }

    public static ConfigParserRegistry getStandard() {
        return standard;
    }
}
